/*
* (c) Copyright IBM Corporation 2018
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

'use strict';

// Use the inbuilt HTTPS module. There are simplified alternatives,
// but this minimises the dependency list.
var https=require('https')

// All the admin REST calls start from this point
var apiBase="/ibmmq/rest/v1/"

// Who am I
var username="app"
var password="_APP_PASSWORD_"

var queue="DEV.QUEUE.1"
var qMgr ="QM1"

// Use basic authentication - pass userid/password on every request
// Could use alternatives with the LTPA token after a call to the /login API
var options = {
  hostname:"localhost",
  port:9443,
  method:'GET',
  headers: {
    'Authorization': 'Basic ' + new Buffer(username + ':' + password).toString('base64'),
    'Content-Type' : 'text/plain',
// Need this header for POST operations even if it has no content
    'ibm-mq-rest-csrf-token' : ''
   }
}

// For test purposes, permit the qmgr to use a self-signed cert. Would
// want to point to a real keystore for secure production
options.rejectUnauthorized = false;

// Construct the full API path from the base for a particular request
options.path = apiBase + "messaging/qmgr/" + qMgr + "/queue/" + queue + "/message"

putMessage()

///////////////////////////////////////////////////////
function putMessage() {
  // And call the operation
  options.method = 'POST'
  var request = https.request(options,(response) => {
    console.log('POST   statusCode : ', response.statusCode);
    response.setEncoding('utf8');

    response.on('data',function(cbresponse) {
       console.log('POST response: ',cbresponse);
    });

    // Once the message has been successfully put, try to get it again
    response.on('end', function() {
        getMessage();
    });
  });

  request.on('error', function (e) {
    console.log('problem with request: ' + e);
  });

  var msg = "Hello world at " + new Date()

  request.write(msg)
  request.end();
}

///////////////////////////////////////////////////////
function getMessage() {
  // Call the operation
  options.method = 'DELETE'
  var request = https.request(options,(response) => {
    console.log('DELETE statusCode : ', response.statusCode);
    response.setEncoding('utf8');
    response.on('data',function(cbresponse) {
       console.log('Message is <%s>',cbresponse);
    });
  });

  request.on('error', function (e) {
    console.log('problem with request: ' + e);
  });

  request.end();
}

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

// This Node.js program uses the MQ Messaging REST API to put and
// get a message. The program uses Promises to control the
// sequence of operations.

const util=require('util');

// Use the inbuilt HTTPS module. There are simplified alternatives,
// but this minimises the dependency list.
const https=require('https');

// All the MQ REST calls - both messaging and admin - start from this path.
const apiBase="/ibmmq/rest/v1/";

// Who am I
const username="app";
const password="_APP_PASSWORD_";

// Where do we connect and which queue to use
const hostname = "localhost";
const port = 9443;
const qMgr ="QM1";
const queue="DEV.QUEUE.1";

// This structure contains the fields needed to control the REST request.
//
// For simple security, this uses basic authentication - pass userid/password
// on every request. Could use alternatives with the LTPA token after a call
// to the /login API
var options = {
  hostname:hostname,
  port:port,
  method:'GET',
  headers: {
    'Authorization': 'Basic ' + new Buffer(username + ':' + password).toString('base64'),
    'Content-Type' : 'text/plain',
// Need this header for POST operations even if it has no content
    'ibm-mq-rest-csrf-token' : ''
   }
};

// For test purposes, permit the qmgr to use a self-signed cert. Would
// want to point to a real keystore and truststore for secure production
options.rejectUnauthorized = false;

// Construct the full API path from the base for a PUT or GET request
options.path = apiBase + "messaging/qmgr/" + qMgr + "/queue/" + queue + "/message";

//
// Define the function to be used when putting a message. The
// function returns a Promise so it can be used in sequence
// before the retrieval.
const putMessage = function(msg) {
  return new Promise((resolve,reject) =>  {
    options.method = 'POST';

    // Create the request and aggregate any returned data
    var request = https.request(options,(response) => {
      if (response.statusCode < 200 || response.statusCode > 299) {
        var errMsg = util.format("POST failed.\nStatusCode: %d\nStatusMessage: \n",response.statusCode, response.statusMessage);
        reject(new Error(errMsg));

      } else {
        console.log('POST   statusCode: ' + response.statusCode);
      }
      var body = '';
      response.on('data',(chunk) => body +=chunk);
      response.on('end', () => resolve());
    });

    request.on('error', (error) => reject(error));
    // Send the message contents
    request.write(msg);
    request.end();
  });
};

// Define a function used to retrieve a message. Use
// a Promise to control when it's ready to print the
// contents.
const getMessage = function() {
  return new Promise((resolve,reject) => {
    options.method = 'DELETE';

    // Create the request and aggregate any returned data
    var request = https.request(options,(response) => {
      if (response.statusCode < 200 || response.statusCode > 299) {
        var errMsg = util.format("DELETE failed.\nStatusCode: %d\nStatusMessage: ",response.statusCode, response.statusMessage);
        reject(new Error(errMsg));
      } else {
        console.log('DELETE statusCode: ' + response.statusCode);
      }
      var body = ''; // The message contents
      response.on('data',(chunk) => body +=chunk);
      response.on('end', () => resolve(body));
    });
    request.on('error', (error) => reject(error));
    request.end();
  });
};

// Create message contents
var msg = "Hello world at " + new Date();

// Put the message and then try to retrieve it. Using Promises
// simplifies the flow and error handling.
putMessage(msg)
  .then(()   => getMessage())
  .then(body => console.log(body))
  .catch(err => console.error(err));

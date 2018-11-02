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

package main

import(
	"fmt"
	"net/http"
	"bytes"
	"crypto/tls"
	"io/ioutil"
	"strconv"
	"os"
)

var apiBase = "/ibmmq/rest/v1/"
// Who am I?
var username = "app"
var password = "test"
// Queue details
var queue = "DEV.QUEUE.1"
var queueManager = "QM1"
var host = "localhost"
var port = "9443"
// Message details
var requestTimeout = 5000
var msgToSend = "Hey World, how's it going?"

func main() {
	// Endpoint for REST API
	URLpath := "https://" + host + ":" + port + apiBase + "messaging/qmgr/" + queueManager + "/queue/" + queue + "/message";
	
	fmt.Print("POSTing...\n")
	// USE ONLY IN DEV... allows skipping of certificates
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
	}
	client := &http.Client{Transport: tr}

	// Preparing for request
	msgByteSlice := []byte(msgToSend)
	req, err := http.NewRequest("POST", URLpath, bytes.NewBuffer(msgByteSlice))

	if err != nil {
		fmt.Printf("Error preparing for REST call: %v\nTerminating...\n", err)
		os.Exit(1)	// Exit ungracefully
	}

	// Add headers & basic authorisation
	req.Header.Add("ibm-mq-rest-csrf-token", "anyvalue")
	req.Header.Add("Content-Type", "application/json")
	req.SetBasicAuth(username, password)
	// Make the request
	resp, err := client.Do(req)

	if resp.StatusCode != 201 {
		fmt.Printf("Error making REST call: %v\nTerminating...\n", resp.Status)
		os.Exit(1)	// Exit ungracefully
	}
	// Read in response
	body, err := ioutil.ReadAll(resp.Body)

	if err != nil {
		fmt.Printf("Error reading resp.Body: %v\nTerminating...\n", err)
		os.Exit(1)	// Exit ungracefully
	}
	
	fmt.Print("POST Successful\n")
	fmt.Print("GETing...\n")
	// Set up timeout for request
	queryParams := "?wait=" + strconv.Itoa(requestTimeout)
	// Preparing for request
	msgByteSlice = []byte("")
	URLpath = URLpath + queryParams
	req, err = http.NewRequest("DELETE", URLpath, bytes.NewBuffer(msgByteSlice))
	// Add headers & basic authorisation
	req.Header.Add("ibm-mq-rest-csrf-token", "anyvalue")
	req.Header.Add("Content-Type", "application/json")
	req.SetBasicAuth(username, password)

	if err != nil {
		fmt.Printf("Error preparing for REST call: %v\nTerminating...\n", err)
		os.Exit(1)	// Exit ungracefully
	}
	// Make the request
	resp, err = client.Do(req)

	if resp.StatusCode != 200 {
		fmt.Printf("Error making REST call: %v\nTerminating...\n", resp.Status)
		os.Exit(1)	// Exit ungracefully
	}
	// Read in response
	body, err = ioutil.ReadAll(resp.Body)

	if err != nil {
		fmt.Printf("Error reading resp.Body: %v\nTerminating...\n", err)
		os.Exit(1)	// Exit ungracefully
	}

	if body == nil {
		fmt.Printf("Response from REST call was nil!\nTerminating...\n")
		os.Exit(1)	// Exit ungracefully
	}
 
	fmt.Printf("GET Successful\n\n%s\n\n", string(body))
}
# Running the Ubuntu install bash script

This bash script is used to install an instance of MQ Advanced for Developers locally onto an Ubuntu machine.

It was discussed in [this tutorial](https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-ubuntu/).

## What the script does

The install script:

1. Downloads IBM MQ Advanced for Developers
2. Installs IBM MQ and prints the installed version
3. Creates and starts a queue manager
4. Creates MQ objects for the queue manager to use
5. Gives permission for "mqclient" group members to connect to the queue manager.

## User/group creation

As mentioned in the tutorial, before using the script, you will need to create a new user account "app" and a new group "mqclient". Commands should look like this:

```bash
sudo addgroup mqclient
sudo adduser app
sudo adduser app mqclient
```

## Get and run the script

Download the script (e.g. with a wget of the raw file URL).
Once this is done, run the command

```bash
chmod 755 mq-ubuntu-install.sh
```

The script can now be executed with

```bash
sudo ./mq-ubuntu-install.sh
```

## Next steps

Go back to the Ubuntu tutorial ([which can be found here](https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-ubuntu/)). Follow the steps in Section 6 to put and get messages to and from a queue.

## License

(c) Copyright IBM Corporation 2018

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License [here](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
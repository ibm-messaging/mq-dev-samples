#   Copyright 2025 IBM Corp.
 
#   Licensed under the Apache License, Version 2.0 (the 'License');
#   you may not use this file except in compliance with the License.
#   You may obtain a copy of the License at
 
#   http://www.apache.org/licenses/LICENSE-2.0
 
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
 

import pymqi

queue_manager = 'QM1'
queue_name = 'DEV.QUEUE.1'
message = 'Hello from Python!'

cd = pymqi.CD()
cd.ChannelName = b'DEV.APP.SVRCONN'
cd.ConnectionName = b'localhost(1414)'
cd.ChannelType = pymqi.CMQC.MQCHT_CLNTCONN
cd.TransportType = pymqi.CMQC.MQXPT_TCP

#cd.SSLCipherSpec = b'TLS_AES_256_GCM_SHA384'
#sco = pymqi.SCO()
#sco.KeyRepository = b'[!!your_keystore_location here!!]' # include file name but not file extension

qmgr = pymqi.QueueManager(None)
qmgr.connect_with_options(queue_manager, user='app', password='[!!password for user "app"!!]', cd=cd)

put_queue = pymqi.Queue(qmgr, queue_name)
put_queue.put(message)

get_queue = pymqi.Queue(qmgr, queue_name)
print(get_queue.get().decode('utf-8'))

put_queue.close()
get_queue.close()

qmgr.disconnect()

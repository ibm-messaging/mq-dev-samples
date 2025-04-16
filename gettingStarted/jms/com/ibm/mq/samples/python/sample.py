import pymqi

queue_manager = 'QM1'
queue_name = 'DEV.QUEUE.1'
message = 'Hello from Python!'

cd = pymqi.CD()
cd.ChannelName = b'DEV.APP.SVRCONN'
cd.ConnectionName = b'localhost(1414)'
cd.ChannelType = pymqi.CMQC.MQCHT_CLNTCONN
cd.TransportType = pymqi.CMQC.MQXPT_TCP

//cd.SSLCipherSpec = b'TLS_AES_256_GCM_SHA384'
//sco = pymqi.SCO()
//sco.KeyRepository = b'[!!your_keystore_location here!!]' # include file name but not file extension

qmgr = pymqi.QueueManager(None)
qmgr.connect_with_options(queue_manager, user='app', password='[!!password for user "app"!!]', cd=cd)

put_queue = pymqi.Queue(qmgr, queue_name)
put_queue.put(message)

get_queue = pymqi.Queue(qmgr, queue_name)
print(get_queue.get().decode('utf-8'))

put_queue.close()
get_queue.close()

qmgr.disconnect()

#-*-coding:utf-8-*-
# client
import socket
import time
sk = socket.socket()
sk.connect(('127.0.0.1',30000))
msg = sk.recv(1024).decode('utf-8')  # 最多接受1024字节
print(msg)
sk.send('你好'.encode('utf-8'))
while True:
    msg = sk.recv(1024).decode('utf-8')
    time.sleep(1)
    if msg != "":
        print(msg)
    else:
        print("waiting...")
sk.close()




# UDP dht port snitch.
#   2021/03/21 - mayton : In beginning

import socket
import bencodepy
import binascii

UDP_IP = "0.0.0.0"
UDP_PORT = 51413

sock = socket.socket(socket.AF_INET, # Internet
                     socket.SOCK_DGRAM) # UDP

sock.bind((UDP_IP, UDP_PORT))

print("Listening ip = ", UDP_IP, " port = ", UDP_PORT)

while True:
    data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes
    try:
        print("Decoded UDP content is : " , bencodepy.decode(data))
    except bencodepy.exceptions.DecodingError as e:
        print("Exception", e.__class__, " Unable to decode UDP data : " ,  binascii.hexlify(data))
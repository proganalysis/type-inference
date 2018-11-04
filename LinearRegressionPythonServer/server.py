from phe import paillier
import socket

pub_key = paillier.PaillierPublicKey(16519204823714427817)
pvt_key = paillier.PaillierPrivateKey(pub_key, 4115582333, 4013819549)

# 24455590686746858144017520591119508899,96694363427786919311447783664600134886,125937181882561400414764924588964904158,20693127773932846497370217358409579456,194579232799624254134219538350604406998

# test = 52435254223724126963066356287370695411
# test_enc = paillier.EncryptedNumber(pub_key, test, 1)
# print(pvt_key.raw_decrypt(52435254223724126963066356287370695411))
#
# test2 = 500
# print(pub_key.raw_encrypt(test2))

connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
connection.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
connection.bind(('0.0.0.0', 44444))
connection.listen(10)

while True:
    current_connection, address = connection.accept()
    data = current_connection.recv(4096).decode("utf-8")
    data_split = data.split("|")
    op = data_split[0]
    a = int(data_split[1])
    b = int(data_split[2])
    a_raw = pvt_key.raw_decrypt(a)
    b_raw = pvt_key.raw_decrypt(b)
    print("a_raw: {} b_raw: {}".format(a_raw, b_raw))
    if op == "MH":
        c_raw = a_raw * b_raw
        print('c_raw: {}'.format(c_raw))
        c = pub_key.raw_encrypt(c_raw)
        # print('c: {}'.format(c))
        current_connection.send(str(c).encode("utf-8"))
        current_connection.close()

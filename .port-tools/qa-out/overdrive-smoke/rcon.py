import socket,struct,sys
port=int(sys.argv[1]); password=sys.argv[2]
def packet(sock,kind,text):
    data=struct.pack('<ii',1,kind)+text.encode()+b'\0\0'
    sock.sendall(struct.pack('<i',len(data))+data)
    def read(n):
        data=b''
        while len(data)<n:
            part=sock.recv(n-len(data))
            if not part: raise RuntimeError('RCON connection closed')
            data+=part
        return data
    length=struct.unpack('<i',read(4))[0]
    response=read(length)
    ident,typ=struct.unpack('<ii',response[:8])
    if ident==-1: raise RuntimeError('RCON authentication failed')
    print(response[8:-2].decode(errors='replace'))
with socket.create_connection(('127.0.0.1',port),timeout=15) as sock:
    packet(sock,3,password)
    for command in sys.argv[3:]: packet(sock,2,command)

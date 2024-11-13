import selectors
import socket
import argparse

parser = argparse.ArgumentParser()

parser.add_argument(
    '--port',
    type=int,
    help="Which port to bind",
    default=9000
)

args = parser.parse_args()

HOST = '127.0.0.1'
PORT = args.port
sock_agent = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock_agent.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
sock_agent.bind((HOST, PORT))
sock_agent.listen()
print('Listening on', (HOST, PORT))
sock_agent.setblocking(False)

sel = selectors.DefaultSelector()
sel.register(sock_agent, selectors.EVENT_READ, data=None)
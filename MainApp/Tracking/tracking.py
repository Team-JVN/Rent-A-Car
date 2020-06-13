import pika
import time
import json

class Location(object):
    def __init__(self, lat,lon):
        self.lat = lat
        self.lon = lon


connection = pika.BlockingConnection(
    pika.ConnectionParameters(host='localhost'))
channel = connection.channel()

channel.queue_declare(queue='tracking')


points = [Location(1.1, 2.2), Location(1.2, 3.3), Location(9.0, 8.9)]

i = 0
while True:
    print(points[i].lat, points[i].lon)
    message = json.dumps(points[i].__dict__)
    channel.basic_publish(exchange='', routing_key='tracking', body=message)
    if i == (len(points) - 1):
        i = 0
    else:
        i = i + 1
    print(i)
    print("--------------------------------------")
    time.sleep(10)

connection.close()

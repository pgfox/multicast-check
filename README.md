# multicast-check
NOTE: not throughly tested - work in progress.

quick util class to check if multicast is available on the network
The following command with create a multicast broadcaster (sending messages to the multicast group) and a multicast listener that listens on the same group

~~~
java -DCLIENT_ID=XXXX -jar multicast-check-1.0-SNAPSHOT.jar
~~~
XXXX to be replaced with your identifier of choice.

Other options that can be set

-DMULTICAST_ADDRESS=XXX.XXX.XXX.XXX 
address of the multicast group. Default value 231.7.7.7 

-DGROUP_PORT=XXXX
port used by the multicast group. Default is 9876

-DSEND_DELAY=X
Deplays between broadcasting a datagram to the multicast group. X is in seconds and the default delay is 5 seconds




SAMPLE OUTPUT
~~~

Using MULTICAST_ADDRESS 231.7.7.7
Using GROUP_PORT 9876
Using CLIENT_ID 5555
Using SEND_DELAY 5
Broadcaster and Listener STARTED. Press enter to finish.
Fri Aug 11 14:24:56 CEST 2017 BROADCASTER: Packet sent with client ID 5555
Fri Aug 11 14:25:01 CEST 2017 BROADCASTER: Packet sent with client ID 5555
Fri Aug 11 14:25:01 CEST 2017 LISTENER: Received packet from 5555
Fri Aug 11 14:25:06 CEST 2017 BROADCASTER: Packet sent with client ID 5555
Fri Aug 11 14:25:06 CEST 2017 LISTENER: Received packet from 5555

~~~
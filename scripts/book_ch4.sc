s = Server.local.boot;

~ncpid = "nc -lp 7878".unixCmd;
~ncpid.pidRunning;
~ncpid.kill;

~host = NetAddr("localhost", 7878);
~host.connect;
~host.sendMsg("/tcpTest", "hello");
~host.disconnect;


# gosocket4j

Gosocket Java client lib. Gosocket is A simple, lightweight, session, heartbeat socket library.


## Installation

### Maven

// TODO:



## Usage
```Java
        String host = "127.0.0.1";
        int port = 8888;

        TCPClient<String> cli = new TCPClient<>(host, port,
                new Codec<String>() {
                    @Override
                    public byte[] encode(String message) throws CodecException {
                        return message.getBytes();
                    }

                    @Override
                    public String decode(byte[] body) throws CodecException {
                        return new String(body);
                    }
                }, (m, c) ->
                c.dLog().debug(format("Cli %s Received message: %s", c.name(), m)));

        cli.setRunModeAsDebug(true).dial();

        cli.sendMessage("Hello Gosocket!");

        cli.hangup(null);
```

## Gosocket Server
see: [gosocket](https://github.com/thiinbit/gosocket)



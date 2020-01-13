# Trdecord-API - a Spring webserver

This project defines a simple Spring webserver that connects to a Corda node via RPC. It uses
Spring Security as the repository for security tokens. There is a trivial ThymeLeaf front end

## License
This code is provided as-is by Osiris Consultants Ltd. There is no support expressed or 
implied. Any support, extensions, etc will be treated as requests for billable consultancy. 
Contact Osiris for further information. 

adrian.challinor@osiris.co.uk 

# Notes 
1. We use a plaintext (null) password encoder. This is insecure, but it is necessary in order 
to pass the password over to the Corda RPC. A later version may see about having this encoded and 
only decoded when necessary.   

2. The version of Spring Boot is important. Attempting to use the absolute latest version 
will stop the execution. Note that this happens in Spring Tool Suite (STS) as well. 

3. The gradle build takes out a number of logging libraries that R3 include. this solves some 
of the error messages that the Corda Spring Boot webserver experiences. 

# Structure:

The Spring web server is defined in the `server` module, and has following components

* `src/main/resources/tempplates`, which defines the webserver's frontend
* `src/main/ksotlin/uk/co/osiris/server`, which defines the webserver's backend

within this second directory, there is a module called `security` that handles the interface to 
Spring Security.  The components here are all named `Corda....` are extend Spring Security 
components. They extend the basic username/password model to include the server and port. 

A customised CordaUser includes the CordFPC object once security checks have passed.

Note the `CordaSecurityConfig` This defines a form, `login` that will be called when access to any 
secured page is requested (which is all of them!). It also defined a custom logout process that will 
cleanly close the connection to the Corda RPC. Not calling this risks memory leakages.      

Within this: 

The backend has one controller, defined in `server/src/main/kotlin/uk/co/osiris//server/Controllers/`:

* `StandardController`, which provides generic (non-CorDapp specific) REST endpoints. Note that the 
`/status` page uses ThymeLeaf to display a number of Corda details.  



## Running the webservers:

Once the nodes are running, there are several ways to run the webservers. All these approaches 
read their properties from the `src/main/resources/application.properties` file:

* `server.port`, which defines the HTTP port the webserver listens on
* `config.rpc.*`, which define the RPC settings the webserver uses to connect to the node

 
## Running the nodes:
You all know this by now. 
See https://docs.corda.net/tutorial-cordapp.html#running-the-example-cordapp.

## Building 
Either import this project in to Intellij or use gradle. Once built, you can use Intellih to `run Server` or
you can use gradle with runServer as the task. 
 
Once the nodes are started, you can access the node's frontend at the following address:

    localhost:10055

And you can access the REST endpoints at:

    localhost:10055/[ENDPOINT]

For example, you can check the node's status using:

    localhost:10055/status

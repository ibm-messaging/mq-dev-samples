# IBM MQ .NET sample to Imitate the Serialization and Deserialization of Objects using JSON and XML Serializers

## Overview

Microsoft has removed support for BinaryFormatter, which IBM MQ relied on for the `WriteObject` and `ReadObject` methods used in .NET object serialization and de-serialization. As a result, these methods are deprecated starting with MQ 9.4.4.
Therefore, Application developers must adopt a new strategy for serializing and de-serializing objects.

The sample demonstrates how to perform .NET object serialization and de-serialization using native .NET capabilities based on XML and JSON formats.

**IBM MQ Documentation : [Deprecated, stabilized and removed features in IBM MQ 9.4.4](https://www.ibm.com/docs/en/ibm-mq/9.4.x?topic=944-deprecated-stabilized-removed-features-in-mq)**

These are the two main types of serializers that can be used as replacements for BinaryFormatter.

1. JSON Serialization using "System.Text.Json"
2. XML Serialization using "XmlSerializer"

### JSON Serialization (System.Text.Json)

- What it is : Converts objects into JSON (JavaScript Object Notation).
- Format : Human-readable, lightweight, widely used in APIs and web applications.
- Serialization : Converts an object into a JSON string, which can be saved into a .json file.
- Deserialization : Reads the JSON string and reconstructs the original object.
- Advantages :
  1. Fast and efficient
  2. Built into modern .NET versions (no extra packages required)
  3. Widely supported across platforms

- Best for : Web APIs, config files, data exchange with JavaScript or REST services.

### XML Serialization (System.Xml.Serialization)

- What it is : Converts objects into XML (Extensible Markup Language).
- Format : More verbose, but standardized and supported by many systems.
- Serialization : Converts an object into XML format, which can be saved into a .xml file.
- Deserialization : Reads the XML content and reconstructs the object.
- Advantages :
  1. Human-readable with strong schema support
  2. Good for interoperability with legacy systems
  3. Can be validated against XSD schemas

- Best for : Configurations, enterprise systems, or when working with systems that require XML.

### Sample Overview

The .NET sample is based on the existing samples shipped with IBM MQ Server and Client Packages. The sample here have been tested with .NET 8.0 and Visual Studio 2023 v 25.0.1706.3. Also it has been tested with Visual Studio for Mac 2023 v 25.0.1706.3.

We have included '.sln', '.csproj', 'packages.config'. These were created with the Visual Studio Community 2023. We've also added the copy of the 'env.json' file from the top level directory to the '/dotnet' project directory, the files will be copied to '/bin/Debug/net8.0' and adjust the parameters to use your own queue manager.
The User can also provide their external env.json by setting the environment variable ENV_FILE.

## References from Visual Studio

- **[IBM MQ .NET Client package](https://www.nuget.org/packages/IBMMQDotnetClient) : Used to make MQ API calls.**
- **[Newtonsoft JSON package](https://www.nuget.org/packages/Newtonsoft.Json/) : Used for Reading the Endpoints from the provided `env.json` file.**

Reference it through Solution Explorer NuGet Package installer

## JSON-Serialization and JSON-Deserialization

We use the JSON serializer and deserializer available in the `System.Text.Json` package.
Objects are serialized before performing a "Put" operation on the queue, and deserialized after performing a "Get" operation.

Both operations have been tested through the provided project.

To run the project, execute the `.exe` file from the command line.  
Navigate to the appropriate directory based on your build configuration:

- `bin\Debug\` (for Debug builds)
- `bin\Release\` (for Release builds)

For Example :

- `IBM_MQ_dotNet_JSON_XML_serialization_sample_utility.exe putjson` - Puts a JSON-serialized object onto an IBM MQ queue.
- `IBM_MQ_dotNet_JSON_XML_serialization_sample_utility.exe getjson` - Retrieves and deserializes a JSON-serialized message from an IBM MQ queue.

## XML-Serialization and XML-Deserialization

We use the XML serializer and deserializer available in the `System.Xml.Serialization` package.
Objects are serialized before performing a "Put" operation on the queue, and deserialized after performing a "Get" operation.

Both operations have been tested through the provided project.

To run the project, execute the `.exe` file from the command line.  
Navigate to the appropriate directory based on your build configuration:

- `bin\Debug\` (for Debug builds)
- `bin\Release\` (for Release builds)

For Example :

- `IBM_MQ_dotNet_JSON_XML_serialization_sample_utility.exe putxml` - Puts a XML-serialized object onto an IBM MQ queue.
- `IBM_MQ_dotNet_JSON_XML_serialization_sample_utility.exe getxml` - Retrieves and deserializes a XML-serialized message from an IBM MQ queue.

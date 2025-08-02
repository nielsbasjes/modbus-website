+++
title = 'GraphQL'
weight = 60
+++

Only being able to use Modbus and give meaning to the registers in a local application is a good start. It really becomes interesting when existing tools are able to consume the data without doing any magic. 

For this purpose I also created a GraphQL wrapper that is able to use the Modbus Schema and convert that into a GraphQL service. 

Currently two docker images have been created that are available on the docker hub.

- **nielsbasjes/modbus-tcp-graphql**
  - An image that is able to bridge between a modbus TCP device based on the provided Modbus Schema
- **nielsbasjes/sunspec-graphql**
  - An image that is able to bridge between a SunSpec (over modbus TCP) device where the schema is based upon SunSpec.

## Overview
This library takes the Modbus Schema and converts it to a GraphQL schema. 
This should allow any (valid) Modbus Schema to be converted. 

This supports doing a `query` and doing a `subscription`. This means you can get both a single set of values and choose to get automatic updates every few seconds (configurable).

In addition to the blocks and fields you requested you can also get information about the actual modbus calls that were done.

## Serving a Modbus TCP device over GraphQL
The starting point is the modbus schema file like this minimal example:
{{< notice style="green" icon="screwdriver-wrench" title="minimal.yaml" expanded="true" >}}
{{< code language="yaml" source="/code/graphql/modbus-tcp/minimal.yaml" >}}
{{< /notice >}}

Now create a docker compose config which 
- maps the Modbus Schema file (here minimal.yaml) into the docker container.
- provides the modbus parameters to connect to it (i.e. hostname, port and unitid).

{{< notice style="green" icon="screwdriver-wrench" title="docker-compose.yml" expanded="true" >}}
{{< code language="yaml" source="/code/graphql/modbus-tcp/docker-compose.yml" >}}
{{< /notice >}}

## Serving a SunSpec (Modbus TCP) device over GraphQL
Because the schema for SunSpec is special you do not need to provide it.
Just point the service to your device and go from there.

Now create a docker compose config which
- provides the modbus parameters to connect to it (i.e. hostname, port and unitid).

{{< notice style="green" icon="screwdriver-wrench" title="docker-compose.yml" expanded="true" >}}
{{< code language="yaml" source="/code/graphql/sunspec/docker-compose.yml" >}}
{{< /notice >}}

## Using GraphQL
When you run the GraphQL service and open `http://localhost:8080` you get a GraphiQL interface that allows you to manually try everything out. 

Then something like this can be queried via GraphQL:
```graphql
query {
    deviceData {
        totalUpdateDurationMs
        modbusQueries {
            start
            count
            fields
            status
            duration
        }
        block1 {
            name
        }
    }
}
```

The result in this case looks like this:

```json
{
  "data": {
    "deviceData": {
      "totalUpdateDurationMs": 522,
      "modbusQueries": [
        {
          "start": "hr:00000",
          "count": 12,
          "fields": [
            "Block 1|Name"
          ],
          "status": "SUCCESS",
          "duration": 522
        }
      ],
      "block1": {
        "name": "Niels Basjes"
      }
    }
  }
}
```
So apparently it took 522ms to make 1 modbus request for 12 registers.

Now I do the same query again with max age `1 hour`:
```graphql
query {
    deviceData(maxAgeMs: 60000) {
        totalUpdateDurationMs
        modbusQueries {
            start
            count
            fields
            status
            duration
        }
        block1 {
            name
        }
    }
}
```

Then the output (IF you do it within the specified timeframe) looks like this:

```json
{
  "data": {
    "deviceData": {
      "totalUpdateDurationMs": 0,
      "modbusQueries": [],
      "block1": {
        "name": "Niels Basjes"
      }
    }
  }
}
```

So all the requested values were returned and no Modbus queries were done to do that.

And if you want to receive continuous updates you can do something like this:
```graphql
subscription {
  deviceData(maxAgeMs: 1000, intervalMs:5000) {
    block1 {
      name
    }
  }
}
```
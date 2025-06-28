+++
title = 'GraphQL'
weight = 20
+++
# Serving a SunSpec (Modbus TCP) device over GraphQL
For SunSpec a very simple to use docker image is available that allows you to create a (read only) GraphQL interface around any SunSpec device.

Just create a docker compose config which provides the modbus parameters to connect to it (i.e. hostname, port and unitid) like this.

{{< notice style="green" icon="screwdriver-wrench" title="docker-compose.yml" expanded="true" >}}
{{< code language="yaml" source="/code/graphql/sunspec/docker-compose.yml" >}}
{{< /notice >}}

When you run this you'll get a GraphiQL UI at http://localhost:8080

For more information have a look at the [GraphQL](/graphql) page.
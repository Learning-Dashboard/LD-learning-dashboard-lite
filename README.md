# Learning Dashboard ![](https://img.shields.io/badge/License-Apache2.0-blue.svg)
Learning Dashboard is a tool to visualize and monitor the achievement of learning objectives in subjects based on the team development of software projects.

## Main Functionality
The main functionalities of the current version of the Learning Dashboard are: providing several ways to visualize and explore the available data, generate predictions of the existing assessments, and perform simulations on how the strategic indicators will evolve based on the value of the factors.

The **User's Guide** is available in the [Wiki](https://github.com/Learning-Dashboard/LD-learning-dashboard/wiki/User-Guide).

## Technologies
| Property                     | Description                            |
|------------------------------|----------------------------------------|
| Type of component            | Web Application                        |
| Build                        | .war                                   |
| Programming language         | Java                                   |
| DBMS                         | PostgreSQL                             |
| Frameworks                   | Spring Boot, AngularJS, Gradle         |
| External libraries           | Chart.js, MongoDB Java API             |
| Learning Dashboard libraries | LD-eval, LD-qma-mongo                  |

## How to build
This is a Gradle project. You can use any IDE that supports Gradle to build it, or alternatively you can use the command line using the Gradle wrapper with the command *__gradlew__* if you don't have Gradle installed on your machine or with the command *__gradle__* if you do, followed by the task *__war__*.

```
# Example: using Gradle wrapper to build with dependencies
cd LD-learning-dashboard
gradlew war
```
After the build is done the WAR file can be found at the __build/libs__ directory

## Docker setup
The Learning Dashboard can be run using Docker. You can find the Dockerfile in the repository, and you can build the image with the following command:

```
docker build -t learning-dashboard .
```
After building the image, you can run the container with the following command:

```
docker run -p 8888:8080 \
  -e SECURITY_JWT_SECRET="$(openssl rand -base64 64)" \
  learning-dashboard
```

## Deploying with the parent Docker Compose

When this project is deployed from the parent `learning-dashboard-infraestructure` / `learning-dashboard-deploy` directory, `docker compose up -d --build` does **not** rebuild the Learning Dashboard WAR.

In the current setup, Docker Compose rebuilds the `tomcat` Docker image from the parent `node-tomcat/dockerfile`, but that Dockerfile only defines the Tomcat base image:

```dockerfile
FROM tomcat:9.0.16-jre8
```

It does not run Gradle, compile this project, or generate `learning-dashboard-3.3.war`. The parent Compose file also mounts Tomcat webapps from the host:

```yaml
${COMPOSE_PROJECT_HOME}/www/api/public:/usr/local/tomcat/webapps
```

That means Tomcat serves whatever WAR is physically present at:

```bash
www/api/public/ROOT.war
```

After changing Java code in this repository, rebuild and redeploy the WAR explicitly:

```bash
cd LD-learning-dashboard-lite

GRADLE_USER_HOME=.gradle-cache ./gradlew bootWar -x test --no-daemon

cd ..

docker compose stop tomcat

rm -rf www/api/public/ROOT
cp LD-learning-dashboard-lite/build/libs/learning-dashboard-3.3.war www/api/public/ROOT.war

docker compose up -d --force-recreate --no-deps tomcat
```

To verify that the deployed WAR contains the latest JWT security changes:

```bash
unzip -l www/api/public/ROOT.war | grep JwtKeyProvider
```

If `JwtKeyProvider.class` appears, the deployed WAR contains the JWT key provider code.

Mental model:

```text
Java changes in the dashboard -> gradlew bootWar + copy ROOT.war
Dockerfile/node-tomcat changes -> docker compose up -d --build tomcat
.env changes -> docker compose up -d --force-recreate tomcat
```

`--build` only rebuilds Docker images. It does not rebuild Gradle artifacts that live outside the Dockerfile.

## Security configuration

JWT signing requires `security.jwt.secret`, supplied externally as configuration or as the `SECURITY_JWT_SECRET` environment variable. Use a random value of at least 64 UTF-8 bytes for HS512 and do not commit it to source control.

## Documentation

You can find the user documentation in the repository [Wiki](https://github.com/Learning-Dashboard/LD-learning-dashboard/wiki) and the technical documentation of the RESTful API [here](https://learning-dashboard.github.io/LD-learning-dashboard).


## Licensing

Software licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

## Contact

For problems regarding this component, please open an issue in the [issues section](https://github.com/Learning-Dashboard/LD-learning-dashboard/issues).

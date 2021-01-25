## prerequisites
If you want to experiment with native executables, please install GraalVM and the ```native-image``` extension instead of or alongside the Java SDK.

## commands available

All the shell scripts inside the ```scripts``` folder can be run setting a number of environment variables.

The available environment variables are:
- ```JAVA_HOME```: (required) the path to the java home used to run Clojure
- ```GRAALVM_HOME```: (optional) the path to the GraalVM home
- ```PROJECT_NAME```: (required) used to define the jar name and the native command
- ```PROJECT_VERSION```: (required) used to define the jar name and the version pushed to the jar repos
- ```CLI_TOOL```: (optional) the preferred tool to invoke the commands (can be one of: ```clj```, ```jar```, ```native```)

If set in a file called ```scripts/config/clj_env.sh``` the will be setup every time one of the shell script inside the ```script``` folder is executed.

To create the ```clj_env.sh``` file interactively, please run ```scripts/config/setup_env.sh```.

The following scripts are also available:
- ```scripts/cli.sh```: runs the code in one of the available modes (```clj```, ```jar```, ```native```)
- ```scripts/tools/build-uberjar.sh```: creates the uberjar under the ```lib``` folder to be run in ```jar``` mode
- ```scripts/tools/build-native.sh```: creates the native command under the root folder to be run in ```native``` mode
- ```scripts/tools/run-linting.sh```: run clj-kondo linting for all the code under the ```src``` and ```test``` folders
- ```scripts/tools/run-tests.sh```: runs all the tests available under the ```test``` folder
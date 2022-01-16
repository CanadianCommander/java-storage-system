#!/bin/bash
pushd $(dirname $0) || exit 1

# check for image. build if it doesn't exist
sudo -E docker image inspect jss-test-runner > /dev/null
if (( $? != 0 )); then
  sudo -E docker build -t jss-test-runner -f test/testRunner.Dockerfile .
fi

# delete container if it exists
sudo -E docker container inspect jss-test-runner-1 > /dev/null
if (( $? == 0)); then
  sudo -E docker container rm jss-test-runner-1
fi

# start testing container
sudo -E docker run -v $(pwd):/opt/source \
 --name jss-test-runner-1 \
 --env-file test/testing.env \
 jss-test-runner

popd || exit 1
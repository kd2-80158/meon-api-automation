#!/bin/bash

# ==============================================
# Modern CLI Test Runner for TestNG Framework
# Author: Software Tester :)
# ==============================================

TESTNG_XML="./src/test/resources/testng.xml"
MVN_CMD="mvn clean test"

print_header() {
  echo "====================================================="
  echo "               API Automation Test Runner           "
  echo "====================================================="
}

print_usage() {
  echo ""
  echo "Usage:"
  echo "  ./run-tests.sh <mode>"
  echo ""
  echo "Mode Options:"
  echo "  smoke        Run only Smoke tests"
  echo "  sanity       Run only Sanity tests"
  echo "  regression   Run full Regression suite"
  echo "  help         Show help menu"
  echo ""
  echo "Examples:"
  echo "  ./run-tests.sh smoke"
  echo "  ./run-tests.sh sanity"
  echo "  ./run-tests.sh regression"
}

run_tests() {
  MODE=$1

  case $MODE in
  smoke)
    echo " Running SMOKE tests..."
    $MVN_CMD -Dsurefire.suiteXmlFiles=$TESTNG_XML -Dgroups=smoke
    ;;
  
  sanity)
    echo " Running SANITY tests..."
    $MVN_CMD -Dsurefire.suiteXmlFiles=$TESTNG_XML -Dgroups=sanity
    ;;
  
  regression)
    echo " Running REGRESSION tests..."
    $MVN_CMD -Dsurefire.suiteXmlFiles=$TESTNG_XML
    ;;
  
  *)
    echo " Invalid mode! Use 'help' to see valid commands."
    exit 1
    ;;
  esac
}

# ==========================
# Main Logic
# ==========================
print_header

if [ $# -eq 0 ]; then
  echo " No arguments provided!"
  print_usage
  exit 1
fi

if [ "$1" == "help" ]; then
  print_usage
  exit 0
fi

run_tests $1

#!/usr/bin/bash

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

rm -rf "$SCRIPT_DIR/target/"
rm -rf "$SCRIPT_DIR/project/target/"
rm -rf "$SCRIPT_DIR/project/project/"
rm -rf "$SCRIPT_DIR/.metals/.tmp/"
rm -rf "$SCRIPT_DIR/project/metals.sbt"
rm -rf "$SCRIPT_DIR/.metals"
rm -rf "$SCRIPT_DIR/.bsp"
rm -rf "$SCRIPT_DIR/.scala-build/"


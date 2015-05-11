#!/usr/bin/env bash

echo "path: $1"
echo "userPass: $2"
echo "userName: $3"

cd $1
echo `echo $2 | git svn dcommit --username $3`
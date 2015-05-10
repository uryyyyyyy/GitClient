#!/usr/bin/env bash

echo `echo $1 | git svn dcommit --username $2`
#! /bin/bash

#PATH TO DATABASE FOLDER
export PGFOLDER=/Users/lengzhang/MyWorkSpace/PostGreSQL_DataBase

#PATH TO DATA FOLDER
export PGDATA=$PGFOLDER/myDB/data

#DATABASE LISTENING PORT
export PGPORT=$1

#DBNAME
export DBNAME=flightDB

psql -h 127.0.0.1 -p $PGPORT $DBNAME

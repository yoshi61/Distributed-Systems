#!/bin/bash

mkdir -p ./test
mkdir -p ./ans
#disable *
set -f
for ((i = 1; i <= 50; i++));
do
    op=$(($RANDOM%4))
    a=$(($RANDOM%10000))
    b=$(($RANDOM%100))
    testName="test_$i.txt"
    testAns="ans_$i.txt"
    while [ $a -lt 1 ]
    do
        a=$(($RANDOM%10000))
    done

    while [ $b -lt 1 ]
    do
        b=$(($RANDOM%100))
    done

    if [ $op -eq 0 ]
    then
        op1='+'
    elif [ $op -eq 1 ]
    then
        op1='-'
    elif [ $op -eq 2 ]
    then
        op1='*'
    else
        op1='/'
    fi

    exp="$a$op1$b"
    pos="$a $b $op1"
    testDest="./test/$testName"
    ansDest="./ans/$testAns"
    echo $pos > $testDest
    echo $(($exp)) > $ansDest
done
#enable *
set +f

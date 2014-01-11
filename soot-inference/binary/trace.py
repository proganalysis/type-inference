#!/usr/bin/env python
import sqlite3
import sys
import re
import signal

global conn
trace_list = []

def signal_handler(signum, frame):
    if conn is not None:
        print_trace()
        conn.close()
    print
    sys.exit(1)

signal.signal(signal.SIGINT, signal_handler)


def load_data(db_script):
    global conn
    conn = sqlite3.connect(":memory:")
    c = conn.cursor()
    f = open(db_script)

    print("Loading data from %s..." % db_script)

    for sql in f:
        try:
            c.execute(sql)
        except:
            print "Error: ", sql
    f.close()
    conn.commit()

def trace_constraint():
    print("\n---------Tracing constraints-----------\n")
    c = conn.cursor()
    while True:
        input_text = raw_input("Enter the constraint ID (Press Ctrl+C to exit): ")
        try:
            cid = int(input_text.strip())
        except ValueError:
            continue

        for row in c.execute("select c0.str, c1.str, c2.str, c3.str from constraints as c0 left outer join constraints as c1 left outer join  constraints as c2 left outer join constraints as c3 where c0.cause_1 = c1.id and c0.cause_2 = c2.id and c0.cause_3 = c3.id and c0.id = %d" % cid):
            print("%10s = %d" % ("id", row[0]))
            print("%10s = %s" % ("identifier", row[1]))
            print("%10s = %s" % ("old", row[2]))
            print("%10s = %s" % ("new", row[3]))
            print("%10s = %s" % ("constraint", row[4]))
            print

def trace_value():
    print("\n---------Tracing values-----------\n")
    c = conn.cursor()
    while True:
        input_text = raw_input("Enter the value ID (Press Ctrl+C to exit): ")
        try:
            vid = int(input_text.strip())
        except ValueError:
            continue

        hasResult = False

        for row in c.execute("select avalues.id, identifier, old, new, str from avalues, traces, constraints where avalues.id = traces.value_id and traces.constraint_id = constraints.id and avalues.id = %d" % vid):
            hasResult = True
            print("%10s = %d" % ("id", row[0]))
            print("%10s = %s" % ("identifier", row[1]))
            print("%10s = %s" % ("old", row[2]))
            print("%10s = %s" % ("new", row[3]))
            print("%10s = %s" % ("constraint", row[4]))
            print
        if hasResult and not vid in trace_list:
            trace_list.append(vid)

def print_trace():
    c = conn.cursor()
    isFirst = True
    print()
    for vid in trace_list:
        for row in c.execute("select id, identifier, annos from avalues where id = %d" % vid):
            if not isFirst:
                print("%6s" % "|")
                print("%6s" % "V")
            else:
                isFirst = False
            print("%6d %-16s %10s" % (row[0], row[1], row[2]))


def main():
    if len(sys.argv) == 1:
        db_script = "./sootOutput/sflow-traces.sql"
    elif len(sys.argv) == 2:
        db_script = sys.argv[1];
    else:
        print("Usage: %s <trace.sql> " % sys.argv[0])
        sys.exit(1)

    load_data(db_script)
    trace_value()


if __name__ == "__main__":
        main()

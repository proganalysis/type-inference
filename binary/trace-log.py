#!/usr/bin/env python
import sqlite3
import sys
import re

auto_mode = True

#codeCodes = {
#    'black':	'0;30',		'bright gray':	'0;37',
#    'blue':		'0;34',		'white':		'1;37',
#    'green':	'0;32',		'bright blue':	'1;34',
#    'cyan':		'0;36',		'bright green':	'1;32',
#    'red':		'0;31',		'bright cyan':	'1;36',
#    'purple':	'0;35',		'bright red':	'1;31',
#    'yellow':	'0;33',		'bright purple':'1;35',
#    'dark gray':'1;30',		'bright yellow':'1;33',
#    'normal':	'0'
#}

#def printc(text, color):
#    """Print in color."""
#    print "\033["+codeCodes[color]+"m"+text+"\033[0m"

#def writec(text, color):
#    """Write to stdout in color."""
#    sys.stdout.write("\033["+codeCodes[color]+"m"+text+"\033[0m")


def print_highlight(text):
    p = re.compile('\(([0-9]+)\)')
    start = 0
    r = p.search(text, start)
    res = ''
    while r != None:
        res = res + text[start:r.start(1)]
        res = res + '\033[0;31m' + text[r.start(1):r.end(1)] + '\033[0m'
        start = r.end(1)
        r = p.search(text, start)
    res = res + text[start:len(text)] + '\n'
    text = res
    p = re.compile('{[^{}]+}')
    start = 0
    r = p.search(text, start)
    res = ''
    while r != None:
        res = res + text[start:r.start(0)]
        res = res + '\033[0;34m' + text[r.start(0):r.end(0)] + '\033[0m'
        start = r.end(0)
        r = p.search(text, start)
    res = res + text[start:len(text)] + '\n'
    sys.stdout.write(res)

#    while r != None:
#        writec(text[start:r.start(1)], 'normal')
#        writec(text[r.start(1):r.end(1)], 'red')
#        start = r.end(1)
#        r = p.search(text, start)
#    writec(text[start:len(text)], 'normal')
#    writec('\n', 'normal')

if len(sys.argv) == 3:
    all_refs_file = sys.argv[1];
    all_trace_file = sys.argv[2];
else:
    all_refs_file = 'infer-output/all-refs.log'
    all_trace_file = 'infer-output/trace.log'

#all_refs_file = 'all-refs.log'
#all_trace_file = 'trace.log'

#conn = sqlite3.connect("trace.db")
conn = sqlite3.connect(":memory:")
c = conn.cursor()
# Create tables
refs_table = 'refs'
trace_table = 'trace'
c.execute('create table ' + refs_table + '(ID INTEGER, NAME TEXT, ANNOS TEXT)')
c.execute('create table ' + trace_table + '(ID INTEGER, NAME TEXT, OLD_ANNOS TEXT, NEW_ANNOS TEXT, CONS TEXT, CAUSEDBY TEXT, CAUSEDID INTEGER)')
conn.commit()
# Dump the data
print 'Loading reference data from ' + all_refs_file + '...'
f = open(all_refs_file)
for line in f:
    sql = 'INSERT INTO ' + refs_table + ' VALUES ('
    is_first = True
    for s in line.split('|'):
        if is_first:
            is_first = False
        else:
            sql = sql + ','
        sql = sql + '\'' + s.strip().replace("\'", "").replace("\n", "")  + '\''
    sql = sql + ')'
#    print sql
    try:
        c.execute(sql)
    except:
        print 'Skip invalid input: ' + line
#        print sql
#        raise
conn.commit()
f.close()
print 'Done'
print 'Loading trace data from ' + all_trace_file + '...'
f = open(all_trace_file)
for line in f:
    sql = 'INSERT INTO ' + trace_table + ' VALUES ('
    is_first = True
    for s in line.split('|'):
        if is_first:
            is_first = False
        else:
            sql = sql + ','
        sql = sql + '\'' + s.strip().replace("\'", "")  + '\''
    sql = sql + ')'
    try:
        c.execute(sql)
    except:
        print 'Skip invalid input: ' + line
#        print sql
#        raise
conn.commit()
f.close()
print 'Done'

ref_id = 0

# Now ready to trace
trace_list = []
while True:
    print ''
    if ref_id == 0 or auto_mode == False:
        input_text = raw_input("Enter the ID you want to trace (Press Enter to exit): ")
        try:
            ref_id = int(input_text.strip())
        except ValueError:
            ref_id = -1
    if ref_id == -1:
        print 'The trace is:\n'
        for rid in trace_list:
            for row in c.execute('SELECT NAME, ANNOS from ' + refs_table + ' where ID = ' + str(rid)):
                print row[0], row[1]
                print '    |   '
                print '    V   '
        print 'Done'
        sys.exit(1)
    if ref_id in trace_list:
        print str(ref_id) + ' is in the trace list. You may have entered it before.'
#        if auto_mode == True:
#            ref_id = -1
#        continue
    has_result = False
    print ''
    caused_id = 0;
    first_id = 0;
    for row in c.execute('SELECT * from ' + trace_table + ' where ID = ' + str(ref_id)):
        has_result = True
        print_highlight(row[1] + ': ')
        print '\t\t\t' + row[2] + ' --> ' + row[3]
        print 'Due to:'
        print_highlight(row[4] + "\nCaused by: " + row[5])
#        print_highlight("caused by " + row[5])
        first_id = caused_id;
        caused_id = row[6]
#        print ''
    if has_result and not ref_id in trace_list:
        trace_list.append(ref_id)
    else:
        print "Not found"
    if first_id == 0 or first_id == caused_id:
        ref_id = caused_id
    else:
        ref_id = 0

conn.close()

import os

instances = ['020', '040', '060', '080', '100', '200', '400']
#instances = ['020']

padrao = 'MAX_QBFPT_TS-PADRAO+BEST'
first = 'MAX_QBFPT_TS-PADRAO+FIRST'
intensification = 'MAX_QBFPT_TS-INTENSIFICATION+BEST'
sorrogate = 'MAX_QBFPT_TS-SORROGATE+BEST'

for instance in instances:
	os.system('touch results/' + padrao + '/' + instance)
	os.system('java -jar ' + padrao + '.jar 5 ' + instance + ' >results/' + padrao + '/' + instance)
	os.system('touch results/' + first + '/' + instance)
	os.system('java -jar ' + first + '.jar 5 ' + instance + ' >results/' + first + '/' + instance)

#	os.system('java -jar ' + padrao + '.jar 10 ' + instance + ' >results/' + padrao + '_TENURE/' + instance)
#	os.system('java -jar ' + intensification + '.jar 10 ' + instance + ' >results/' + intensification + '/' + instance)
#	os.system('java -jar ' + sorrogate + '.jar 10 ' + instance + ' >results/' + sorrogate + '/' + instance)

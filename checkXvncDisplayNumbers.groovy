import jenkins.*
import jenkins.model.Jenkins

if (args.length != 3) {
  println('''
   Error with arguments. Three arguments are required.

   checkXvncDisplayNumbers.groovy <A|P> <warning-threshold> <critical-threshold>

   A: your values are interpreted as absolute.
   P: your values are used as a percentage of the total amount of executors.
   <waning-threshold>: Integer, when below this value script will return warning
   <critical-threshold>: Integer, when below this value script will return critical
          ''')
  return
}

def warningThreshold = 0
def criticalThreshold = 0

Jenkins jenkins = Jenkins.getActiveInstance();
xvncDescriptor = jenkins.getDescriptorByType(hudson.plugins.xvnc.Xvnc.DescriptorImpl.class)

numAvail = xvncDescriptor.maxDisplayNumber - xvncDescriptor.minDisplayNumber

if ( args[0] == 'A') {
  warningThreshold = args[1].toInteger()
  criticalThreshold = args[2].toInteger()
} else if ( args[0] == 'P') {
  warningThreshold = numAvail.div(100) * args[1].toInteger()
  criticalThreshold = numAvail.div(100) * args[2].toInteger()
}

nodesCritical = []
nodesWarning = []

xvncDescriptor.allocators.each {
  numAlloc = it.value.allocatedNumbers.size()
  numFree = numAvail - numAlloc
  if ( criticalThreshold < numFree && numFree <= warningThreshold ) {
    nodesWarning.push(it.key)
  } else if ( numFree < criticalThreshold ) {
    nodesCritical.push(it.key)
  }
}

output = ""

if (nodesWarning.size() != 0) {
  status = 1
  output = output + "Nodes below warning threshold " + nodesWarning.findAll() + " \n"
}
if (nodesCritical.size() != 0) {
  status = 2
  output = output + "Nodes below Critical threshold " + nodesCritical.findAll()
}
if (nodesCritical.size() == 0 && nodesWarning.size() == 0) {
  status 0
  output = "OK - Enough display numbers free"
}

println(output)
return status
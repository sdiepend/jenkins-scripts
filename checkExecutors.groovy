import jenkins.*
import jenkins.model.Jenkins
import hudson.model.Computer
import hudson.model.Executor
import hudson.model.Node
import hudson.plugins.swarm.SwarmSlave

int executorCount = 0
int executorBuilding = 0

if (args.length != 3) {
  println('''
   Error with arguments. Three arguments are required.
   
   checkExecutors.groovy <A|P> <warning-threshold> <critical-threshold>
   
   A: your values are interpreted as absolute.
   P: your values are used as a percentage of the total amount of executors.
   <waning-threshold>: Integer, when below this value script will return warning
   <critical-threshold>: Integer, when below this value script will return critical 
          ''')
  return
}

jenkins = Jenkins.getInstance()

executorCount = jenkins.getNumExecutors()

for (Node node : jenkins.getNodes()) {
  Computer computer = node.toComputer();
  if (computer == null) {
    continue;
  }
  if (!computer.isOffline()) {
    for (Executor e : computer.getExecutors()) {
      executorCount++;
      if (!e.isIdle()) {
        executorBuilding++;
      }
    }
  }
}

def warningThreshold = 0
def criticalThreshold = 0

if ( args[0] == 'A') {
  warningThreshold = args[1].toInteger()
  criticalThreshold = args[2].toInteger()
} else if ( args[0] == 'P') {
  warningThreshold = executorCount.div(100) * args[1].toInteger()
  criticalThreshold = executorCount.div(100) * args[2].toInteger()
}

int executorFree = executorCount - executorBuilding

if (executorFree > warningThreshold) {
  println("OK - Enough executors free")
  return 0
} else if (criticalThreshold < executorFree && executorFree <= warningThreshold ) {
  println("WARNING - Amount of free executors is below warning-level of " + warningThreshold)
  return 1
} else if (executorFree <= criticalThreshold) {
  println("CRITICAL - Amount of free executors below warning-level of " + criticalThreshold)
  return 2
} else {
  println("UNKOWN")
  return 3
}

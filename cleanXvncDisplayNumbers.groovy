import jenkins.*
import jenkins.model.Jenkins

Jenkins jenkins = Jenkins.getActiveInstance();
xvncDescriptor = jenkins.getDescriptorByType(hudson.plugins.xvnc.Xvnc.DescriptorImpl.class)

xvncDescriptor.allocators.each {
  allocator = it.value
  // collect is used to make sure numAlloc is an entire new list and not just a reference to the same list object, otherwise you'll get a
  // concurrentmodification exception
  numAlloc = allocator.allocatedNumbers.collect()

  numAlloc.each {
    allocator.allocatedNumbers.remove(it)
  }
}
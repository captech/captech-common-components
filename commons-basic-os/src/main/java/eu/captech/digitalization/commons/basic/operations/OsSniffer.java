package eu.captech.digitalization.commons.basic.operations;

import eu.captech.digitalization.commons.basic.doc.Preamble;

@Preamble(
        lastModifiedBy = "Eduardo Melgar",
        creationDate = "1/29/13",
        creationTime = "2:48 PM",
        lastModified = "1/29/13"
)
public enum OsSniffer {
    osSniffer;

//    public Map<String, Map<String, Number>> getMemory(String memoryType) throws ExecutionException {
//        if (!memoryType.equals(MEM_MEMORY_TYPE) && !memoryType.equals(SWAP_MEMORY_TYPE)) {
//            throw new UnsupportedOperationException("Operation for memory type '" + memoryType + "' is not supported.");
//        }
//        Map<String, Map<String, Number>> map = new HashMap<>();
//        map.put(memoryType, memSniffer.getMemory(memoryType));
//        return map;
//    }
//
//
//    public Map<String, Map<String, Number>> getAllMemories() throws ExecutionException {
//        Map<String, Map<String, Number>> map = getMemory(MEM_MEMORY_TYPE);
//        map.putAll(getMemory(SWAP_MEMORY_TYPE));
//        return map;
//    }
//
//    public Map<String, Map<String, Number>> getCpuTime() throws ExecutionException {
//        Map<String, Map<String, Number>> map = new HashMap<>();
//        map.put(CPU_INFO_TAG, CpuSniffer.cpuSniffer.getCpuTime());
//        return map;
//    }
//
//
//    private int bytesToMB(long bytes) {
//        return ((int) (bytes / 1048576));
//    }
}

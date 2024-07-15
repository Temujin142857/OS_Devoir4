import java.io.*;

public class FIFO {
    int[] virtualMemory;
    pageEntry[] pageTable;
    int[] physicalMemory;

    int entryPointer=0;

    public int collisionCounter;

    public FIFO(int[] virtualMemory, int[] physicalMemory, pageEntry[] pageTable){
        this.pageTable=pageTable;
        this.physicalMemory=physicalMemory;
        this.virtualMemory=virtualMemory;
        collisionCounter=-this.physicalMemory.length;
    }


    public int reference(int virtualLocation){
        int VMVALUE=virtualMemory[virtualLocation];
        pageTable[VMVALUE].lastUsed=System.currentTimeMillis();
        if(pageTable[VMVALUE].valid){
            return physicalMemory[pageTable[VMVALUE].frame];
        }
        else {
            handleCollision(virtualLocation, VMVALUE, pageTable[VMVALUE].frame!=-1);
        }
        return 0;
    }

    private void handleCollision(int virtualLocation, int VMVALUE, boolean load){
        collisionCounter++;
        if(load) {
            String diskLocation = "src\\Disk\\frame" + String.valueOf(virtualLocation)+".txt";
            for (pageEntry p : pageTable) {
                if (p.frame == entryPointer&&p.valid) {
                    p.valid=false;
                    writeToDisk(diskLocation, physicalMemory[entryPointer]);
                }
            }

            FileReader fr = null;
            try {
                fr = new FileReader(diskLocation);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            BufferedReader br = new BufferedReader(fr);
            try {
                String data = br.readLine();
                physicalMemory[entryPointer] = Integer.parseInt(data);
                pageTable[VMVALUE].frame = entryPointer;
                pageTable[VMVALUE].valid = true;
                incrementEntryPointer();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            collisionCounter++;
            for (pageEntry p : pageTable) {
                if (p.frame == entryPointer) p.valid = false;
            }
            pageTable[VMVALUE].frame = entryPointer;
            pageTable[VMVALUE].valid = true;
            incrementEntryPointer();
        }
    }


    private void writeToDisk(String location, int value){
        try {
            FileWriter myWriter = new FileWriter(location);
            myWriter.write(String.valueOf(value));
            myWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void incrementEntryPointer(){
        entryPointer++;
        entryPointer=entryPointer% physicalMemory.length;
    }

}

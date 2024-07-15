import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LRU {



    int[] virtualMemory;
    pageEntry[] pageTable;
    int[] physicalMemory;
    public int collisionCounter;

    public LRU(int[] virtualMemory, int[] physicalMemory, pageEntry[] pageTable){
        this.pageTable=pageTable;
        this.physicalMemory=physicalMemory;
        this.virtualMemory=virtualMemory;
        collisionCounter=-this.physicalMemory.length;
    }


    public int reference(int virtualLocation){
        int VMVALUE=virtualMemory[virtualLocation];
        if(pageTable[VMVALUE].valid){
            pageTable[VMVALUE].lastUsed=System.nanoTime();
            return physicalMemory[pageTable[VMVALUE].frame];
        }
        else {
            handleCollision(virtualLocation, VMVALUE, pageTable[VMVALUE].frame!=-1);
            pageTable[VMVALUE].lastUsed=System.nanoTime();
        }
        return 0;
    }

    private void handleCollision(int virtualLocation, int VMVALUE, boolean load){
        int targetFrame=nextFrameToReplace();
        collisionCounter++;
        if(load) {
            String diskLocation = "src\\Disk\\frame" + String.valueOf(virtualLocation)+".txt";
            for (pageEntry p : pageTable) {
                if (p.frame == targetFrame&&p.valid) {
                    p.valid=false;
                    writeToDisk(diskLocation, physicalMemory[targetFrame]);
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
                physicalMemory[targetFrame] = Integer.parseInt(data);

                pageTable[VMVALUE].frame = targetFrame;
                pageTable[VMVALUE].valid = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            for (pageEntry p : pageTable) {
                if (p.frame == targetFrame) p.valid = false;
            }
            pageTable[VMVALUE].frame = targetFrame;
            pageTable[VMVALUE].valid = true;
        }
        if(false)
        {
            for (pageEntry p:pageTable) {
                System.out.println("frame: " +p.frame+" valid: "+p.valid+ " time: "+p.lastUsed);
            }
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

    private int nextFrameToReplace(){
        long longestTime=Long.MAX_VALUE;
        int frameNum=0;
        ArrayList<Integer> frameList=new ArrayList<>();
        frameList.add(-1);
        int i=0;
        for (pageEntry p:pageTable) {
            if(!frameList.contains(p.frame)){
                frameList.add(p.frame);
            }
            if(p.lastUsed<longestTime&&p.frame!=-1&&p.valid){

                frameNum=p.frame;
                longestTime=p.lastUsed;
            }
            i++;
        }
        if(frameList.size()<=physicalMemory.length){
            frameNum=frameList.size()-1;
        }
        return frameNum;
    }
}

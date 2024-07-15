import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String algorithm;
        String referenceString= "71702976338439024851227201";
        int physicalFrames=5;
        if(args.length>1){
            algorithm=args[0];
            referenceString=args[1];
        }
        else{
            Scanner in = new Scanner(System.in);
            System.out.println("which algorithm? (FIFO or LRU)");
            algorithm=in.nextLine();
            System.out.println("what's the reference string? (press d for default)");
            String line=in.nextLine();
            if(!line.equals("d"))referenceString=line;
            System.out.println("How many physical frames?");
            physicalFrames=in.nextInt();
        }
        int[] physicalMemory=new int[physicalFrames];
        int[] virtualMemory=new int[10];
        pageEntry[] pageTable=new pageEntry[10];
        for (int i = 0; i < 10; i++) {
            pageTable[i]=new pageEntry();
            virtualMemory[i]=i;
        }
        for (int i = 0; i < physicalFrames; i++) {
            physicalMemory[i]=99;
        }

        if(algorithm.equals("FIFO")) {
            FIFO algo = new FIFO(virtualMemory, physicalMemory, pageTable);
            for (String s:referenceString.split("")) {
                algo.reference(Integer.parseInt(String.valueOf(s)));
            }
            System.out.println("collisions= " + algo.collisionCounter);
        }else{
            LRU algo = new LRU(virtualMemory, physicalMemory, pageTable);
            for (String s:referenceString.split("")) {
                algo.reference(Integer.parseInt(String.valueOf(s)));
            }
            System.out.println("collisions= " + algo.collisionCounter);
        }

    }

    public static void scrubDisk(){

    }

}
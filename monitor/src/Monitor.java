class Producer extends Thread { //생산자 클래스
    Monitor monitor;
    String alpha = "abcdefghijklmnopqrstuvwxyz";
    int sleepTime = 200;

    public Producer(Monitor m) {
        monitor = m;
    }

    @Override
    public void run() {
        char c;
        for (int i = 0; i < 10; i++) {
            c = alpha.charAt((int) (Math.random() * 25)); //알파벳의 데이터를 임의로 골라서 저장
            synchronized (monitor) {
                monitor.add(c); //Monitor클래스의 add()메소드를 호출하여 배열에 저장
                System.out.println("생성 -----> " + c + "저장 갯수 : " + monitor.count());
            }
            try {
                Thread.sleep((int) (Math.random() * sleepTime));
                sleepTime = (int) (Math.random() * 1000);
            } catch (InterruptedException ex) {
            }
        }
    }
}

class Customer extends Thread { //소비자 클래스
    Monitor monitor;
    int sleepTime = 500;

    public Customer(Monitor m) {
        monitor = m;
    }

    @Override
    public void run() {
        char c;
        for (int i = 0; i < 10; i++) {
            synchronized (monitor) {
                c = monitor.remove(); //Monitor클래스의 remove()메소드를 호출하여 배열에서 값을 리턴시키고 삭제
                System.out.println("? <----- " + c + "저장 갯수 : " + monitor.count());
            }
            try {
                Thread.sleep((int) (Math.random() * sleepTime));
                sleepTime += (int) (Math.random() * 1000);
            } catch (InterruptedException ex) {
            }
        }
    }
}

public class Monitor {

    char[] buffer = new char[6]; //저장공간
    int removeNext = 0; //지울때 저장공간 index 가리키는 용
    int addNext = 0; ////저장 할때 저장공간 index 가리키는 용
    boolean isFull = false;
    boolean isEmpty = true;

    ///********************* add 함수 구성하기 ******************************
    public void add(char c) {
        if (this.isFull) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.buffer[this.addNext] = c;
        this.addNext = (this.addNext + 1) % 6;
        if ((this.addNext + 1) % 6 == this.removeNext) {
            this.isFull = true;
        } else if (this.isEmpty) {
            this.isEmpty = false;
            this.notify();
        }
    }
    ///*******************************************************************

    ///********************* remove 함수 구성하기 ******************************
    public char remove() {
        if (this.isEmpty) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        char returnCharacter = this.buffer[removeNext];
        this.removeNext = (this.removeNext + 1) % 6;
        if (this.isFull) {
            this.isFull = false;
            this.notify();
        } else if (removeNext == addNext) {
            this.isEmpty = true;
        }
        return returnCharacter;
    }
    //*******************************************************************

    public String count() {//priority Queue형식의 size를 측정 방식을 가지게 하였습니다.
        return String.valueOf((6 + addNext - removeNext) % 6);
    }

    public static void main(String[] args) {
        Monitor m = new Monitor();
        Producer p = new Producer(m);
        Customer c = new Customer(m);

        p.start();
        c.start();
    }
}



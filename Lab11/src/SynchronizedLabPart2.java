
// Isolation

// A shared account
class Account {

    // 3. When the synchronized keyword is removed from the Account class methods, the methods are no longer
    // synchronized, meaning that there is no explicit mutual exclusion mechanism in place to prevent concurrent
    // access to the shared balance variable. While the program may appear to work correctly with small values of n,
    // it becomes increasingly unreliable and prone to issues as the complexity and duration of operations increase.
    private double balance = 0;

    public synchronized void deposit(double amount) {
        balance = balance + amount;
    }
    public synchronized void  withdraw(double amount) {
        balance = balance - amount;
    }
    public synchronized double getBalance() {
        return balance;
    }

}

public class SynchronizedLabPart2 {

    int n = 100000;

    Account acct = new Account();

    // deposit 1,2,3,...,n to the account
    Thread t1 = new Thread( new Runnable() {
        public void run() {
            for(int j = 1; j <= n; j++) {
                acct.deposit(1.0);
            }
        }});

    // withdraw 1,2,3,...,n from the account
    Thread t2 = new Thread( new Runnable() {
        public void run() {
            for(int j = 1; j <= n; j++) {
                acct.withdraw(1.0);
            }
        }});

    public void doBanking() {

        //t1.start();
        //t2.start();
        // 2. Swapping the lines "t1.start();" with "t2.start()" will change the order in which the deposit and withdrawal threads are started.


        // wait for t1 to finish
        try { t1.join(); } catch(InterruptedException e){}
        // wait for t2 to finish
        try { t2.join(); } catch(InterruptedException e){}
        System.out.println("Done waiting");
    }

    public static void main(String[] args) {
        SynchronizedLabPart2 m = new SynchronizedLabPart2();
        m.doBanking();
        System.out.println("Balance should be 0.0  balance = " + m.acct.getBalance());

    }
}

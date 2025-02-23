package bank;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class BankerHelper extends Thread{

    private Socket bankersocket;
    private Map<String,Double> ledger;

    static Semaphore mutex=new Semaphore(1);




    public BankerHelper(Socket bankersocker, Map<String,Double> ledger) {
        this.bankersocket = bankersocker;
        this.ledger=ledger;
    }

    @Override
    public void run() {

        BufferedReader reader=null;
        BufferedWriter writer=null;
            try {
                reader=new BufferedReader(new InputStreamReader(bankersocket.getInputStream()));
                writer=new BufferedWriter(new OutputStreamWriter(bankersocket.getOutputStream()));

                Double code=null;

                String line=reader.readLine();
                String []parts=line.split("\\s+");

                if(parts.length<3){
                    if(parts.length==2){
                        String name=parts[0];
                        String command=parts[1];
                        if(command.equalsIgnoreCase("balance")){
                            if(this.ledger.containsKey(name)){
                                code=ledger.get(name);
                            }
                            else code=-15.0;
                        }
                        else code=-15.0;
                    }
                    else code=-15.0;
                }
                else {
                    String name=parts[0];
                    String command=parts[1];
                    Double amount=Double.parseDouble(parts[2]);

                    if(amount>0){
                        if(ledger.containsKey(name)){
                            if(command.equalsIgnoreCase("withdraw")){

                                mutex.acquire();
                                //TODO synchonisation here
                                double user_balance=ledger.get(name);
                                if(user_balance>amount){
                                    ledger.replace(name,user_balance,user_balance-amount);
                                    code=-1.0;
                                }
                                else {
                                    code=-11.0;
                                }
                                //
                                mutex.release();
                            }
                            else if(command.equalsIgnoreCase("deposit")){
                                if(ledger.containsKey(name)){


                                    //TODO synchonisation here
                                    mutex.acquire();
                                    double user_balance=ledger.get(name);
                                    double sum=amount+user_balance;
                                    ledger.put(name,sum);
                                    mutex.release();
                                }
                                else {
                                    ledger.put(name,amount);
                                }
                                code=-1.0;
                            }
                        }
                        else {
                            if(command.equalsIgnoreCase("withdraw")){
                                code=-12.0;
                            }
                            else if(command.equalsIgnoreCase("deposit")){
                                ledger.put(name,amount);
                                code=-1.0;
                            }
                        }
                    }
                    else code=-15.0;

                }
                writer.write(code+"\n");
                writer.flush();



            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                ServerWorker.deconstructEverything(reader,writer,bankersocket);
        }
    }
}

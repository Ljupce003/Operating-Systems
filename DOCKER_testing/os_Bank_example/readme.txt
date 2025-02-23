So what we will need to implement is a Bank system where we have 3 containers(Server,bank.Banker,Client).
Server sends commands to deposit/withdraw money from an account to the bank.Banker.
And the Server also shares the balances to all clients with the bank.Banker but the banker does the transactions.
The Server only orders the bank.Banker to do the transactions if the client doesn't go to limits.
The CLient sends the command(withdraw,deposit),Client name,amount.
Server then returns a response to the client if the transaction is complete or not


-1-successful deposit
-2-successful withdraw

-10-unsuccessful deposit
-11-unsuccessful withdraw-no money
-12-unsuccessful withdraw-unknown username
-15-wrong input value
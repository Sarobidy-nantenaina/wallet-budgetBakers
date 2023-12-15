package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import model.Account;
import model.Currency;
import model.Transaction;
import repository.AccountCrudOperation;

public class AccountService {

  private final AccountCrudOperation accountCrudOperation;

  public AccountService(AccountCrudOperation accountCrudOperation) {
    this.accountCrudOperation = accountCrudOperation;
  }

  public Account performTransaction(String id, String label, double amount, Transaction.TransactionType transactionType, Currency currency, String accountId) {
    // Obtenez le compte à partir de la base de données
    Account account = accountCrudOperation.findAccountById(accountId);

    if (account != null) {
      // Créez une nouvelle transaction
      Transaction newTransaction = new Transaction(id, label, amount, LocalDateTime.now(), transactionType,accountId);

      // Mettez à jour le solde en fonction du type de transaction
      if (account.getType() == Account.AccountType.BANK || (account.getType() != Account.AccountType.BANK && account.getBalance() >= amount)) {
        // Pour les comptes bancaires ou autres comptes avec un solde suffisant
        if (transactionType == Transaction.TransactionType.DEBIT) {
          // Pour les transactions de débit, le solde peut devenir négatif pour les comptes bancaires
          account.setBalance(account.getBalance() - amount);
        } else {
          // Pour les transactions de crédit, ajoutez au solde
          account.setBalance(account.getBalance() + amount);
        }

        // Ajoutez la transaction à la liste des transactions du compte
        account.getTransactions().add(newTransaction);

        // Mettez à jour la devise du compte
        account.setCurrency(currency);

        // Mettez à jour le compte dans la base de données
        accountCrudOperation.update(account);

        // Retournez les informations mises à jour sur le compte
        return account;
      }
    }

    return null;
  }

  public double getBalanceForAccountAtDatetime(String accountId, LocalDateTime datetime) {
    // Vérifier que l'ID du compte et la date et heure sont valides
    if (accountId == null || datetime == null) {
      throw new IllegalArgumentException("L'ID du compte et la date et heure ne doivent pas être null");
    }

    // Récupération des transactions du compte
    List<Transaction> transactions = accountCrudOperation.findTransactionsByAccountId(accountId);

    // Initialisation du solde initial
    double balance = 0;

    // Parcours des transactions
    for (Transaction transaction : transactions) {
      // Si la transaction a lieu avant la date et heure donnée
      if (transaction.getDateTime().isBefore(datetime)) {
        // Mise à jour du solde
        if (transaction.getType() == Transaction.TransactionType.DEBIT) {
          balance -= transaction.getAmount();
        } else if (transaction.getType() == Transaction.TransactionType.CREDIT) {
          balance += transaction.getAmount();
        }
      }
    }

    // Retourner le solde
    return balance;
  }


  public void transferMoney(Account debitorAccount, Account creditorAccount, double amount) {
    // Vérifiez que le transfert n'est pas effectué vers le même compte
    if (!debitorAccount.getId().equals(creditorAccount.getId())) {
      // Vérifiez que les comptes ont la même devise
      if (debitorAccount.getCurrency() == creditorAccount.getCurrency()) {
        // Ajoutez une transaction de type débit au compte débiteur
        Transaction debitorTransaction = new Transaction(
            UUID.randomUUID().toString(), "Transfer to " + creditorAccount.getName(),
            -amount, LocalDateTime.now(), Transaction.TransactionType.DEBIT, debitorAccount.getId());
        debitorAccount.addTransaction(debitorTransaction);

        // Mettez à jour le solde du compte débiteur
        debitorAccount.setBalance(debitorAccount.getBalance() - amount);
        accountCrudOperation.update(debitorAccount); // Mettez à jour le compte dans la base de données

        // Ajoutez une transaction de type crédit au compte créditeur
        Transaction creditorTransaction = new Transaction(
            UUID.randomUUID().toString(), "Transfer from " + debitorAccount.getName(),
            amount, LocalDateTime.now(), Transaction.TransactionType.CREDIT, creditorAccount.getId());
        creditorAccount.addTransaction(creditorTransaction);

        // Mettez à jour le solde du compte créditeur
        creditorAccount.setBalance(creditorAccount.getBalance() + amount);
        accountCrudOperation.update(creditorAccount); // Mettez à jour le compte dans la base de données

        // Enregistrez l'historique du transfert dans la table TransferHistory
        accountCrudOperation.saveTransferHistory(debitorTransaction.getId(), creditorTransaction.getId(), LocalDateTime.now());
      } else {
        // Gérez le cas où les comptes n'ont pas la même devise
        System.out.println("Impossible de transférer de l'argent entre des comptes de devises différentes.");
      }
    } else {
      // Gérez le cas où le transfert est effectué vers le même compte
      System.out.println("Impossible de transférer de l'argent vers le même compte.");
    }
  }

}

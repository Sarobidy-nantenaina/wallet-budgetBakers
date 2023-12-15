package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.Account;
import model.Currency;
import model.CurrencyValue;
import model.Transaction;
import model.TransferHistory;
import repository.AccountCrudOperation;
import repository.TransactionCrudOperation;
import repository.TransferHistoryCrudOperation;

public class AccountService {

  private final AccountCrudOperation accountCrudOperation;
  private final TransactionCrudOperation transactionCrudOperation;
  private final TransferHistoryCrudOperation transferHistoryCrudOperation;

  public AccountService(AccountCrudOperation accountCrudOperation,TransactionCrudOperation transactionCrudOperation,
                        TransferHistoryCrudOperation transferHistoryCrudOperation) {
    this.accountCrudOperation = accountCrudOperation;
    this.transactionCrudOperation = transactionCrudOperation;
    this.transferHistoryCrudOperation = transferHistoryCrudOperation;
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


  public void transferMoney(String accountIdDebitor, String accountIdCreditor, double amount, LocalDateTime transferDate) {
    // Vérifier que les paramètres sont valides
    if (accountIdDebitor == null || accountIdCreditor == null || amount <= 0 || transferDate == null) {
      throw new IllegalArgumentException("Les paramètres ne doivent pas être null ou non valides");
    }

    // Vérifier que les comptes sont différents
    if (accountIdDebitor.equals(accountIdCreditor)) {
      throw new IllegalArgumentException("Un compte ne peut pas effectuer un transfert vers lui-même");
    }

    Account accountDebitor = accountCrudOperation.findAccountById(accountIdDebitor);
    Account accountCreditor = accountCrudOperation.findAccountById(accountIdCreditor);

    // Vérifier que le compte débiteur dispose d'un solde suffisant
    double balanceDebitor = accountDebitor.getBalance();
    if (balanceDebitor < amount) {
      throw new IllegalArgumentException("Le compte débiteur ne dispose pas d'un solde suffisant");
    }


    // Vérifier que les comptes ont des devises différentes
    if (!accountDebitor.getCurrency().equals(accountCreditor.getCurrency())) {
      // Effectuer la conversion du montant en Ariary
      double exchangeRate = getExchangeRate(transferDate);
      // Effectuer la conversion du montant en Ariary
      double amountInAriary = amount * exchangeRate;

      // Mettre à jour les soldes des comptes
      accountDebitor.setBalance(accountDebitor.getBalance()-amount);
      accountCreditor.setBalance(accountCreditor.getBalance() + amountInAriary);

      // Continuer le processus de transfert avec le montant converti en Ariary
      Transaction transactionDebitor = new Transaction();
      transactionDebitor.setAccount_id(accountIdDebitor);
      transactionDebitor.setAmount(amountInAriary);
      transactionDebitor.setType(Transaction.TransactionType.DEBIT);
      transactionDebitor.setLabel("Transfert vers le compte " + accountIdCreditor);
      transactionCrudOperation.save(transactionDebitor);

      Transaction transactionCreditor = new Transaction();
      transactionCreditor.setAccount_id(accountIdCreditor);
      transactionCreditor.setAmount(amountInAriary);
      transactionCreditor.setType(Transaction.TransactionType.CREDIT);
      transactionCreditor.setLabel("Transfert depuis le compte " + accountIdDebitor);
      transactionCrudOperation.save(transactionCreditor);

      TransferHistory transferHistory = new TransferHistory();
      transferHistory.setDebitorTransactionId(accountIdDebitor);
      transferHistory.setCreditorTransactionId(accountIdCreditor);
      transferHistory.setTransferAmount(amount);
      transferHistory.setTransferDate(transferDate);
      transferHistoryCrudOperation.save(transferHistory);
    } else {
      // Continuer le processus de transfert avec le montant en Euros
      Transaction transactionDebitor = new Transaction();
      transactionDebitor.setAccount_id(accountIdDebitor);
      transactionDebitor.setAmount(amount);
      transactionDebitor.setType(Transaction.TransactionType.DEBIT);
      transactionDebitor.setLabel("Transfert vers le compte " + accountIdCreditor);
      transactionCrudOperation.save(transactionDebitor);

      Transaction transactionCreditor = new Transaction();
      transactionCreditor.setAccount_id(accountIdCreditor);
      transactionCreditor.setAmount(amount);
      transactionCreditor.setType(Transaction.TransactionType.CREDIT);
      transactionCreditor.setLabel("Transfert depuis le compte " + accountIdDebitor);
      transactionCrudOperation.save(transactionCreditor);

      TransferHistory transferHistory = new TransferHistory();
      transferHistory.setDebitorTransactionId(accountIdDebitor);
      transferHistory.setCreditorTransactionId(accountIdCreditor);
      transferHistory.setTransferAmount(amount);
      transferHistory.setTransferDate(transferDate);
      transferHistoryCrudOperation.save(transferHistory);
    }
  }




  public double getExchangeRate(LocalDateTime transferDate) {
    List<CurrencyValue> currencyValues = new ArrayList<>();
    for (CurrencyValue cv : currencyValues) {
      if (cv.getDateValue().equals(transferDate)) {
        return cv.getValue();
      }
    }

    // Si aucune valeur trouvée, lever une exception
    throw new IllegalArgumentException("Le taux de change n'est pas disponible pour la date " + transferDate);
  }

}

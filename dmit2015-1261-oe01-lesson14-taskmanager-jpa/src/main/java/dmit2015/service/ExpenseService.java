package dmit2015.service;

import dmit2015.model.Expense;

import java.util.List;
import java.util.Optional;

public interface ExpenseService {

    Expense createExpense(Expense expense);

    Optional<Expense> getExpenseById(Long id);

    List<Expense> getAllExpenses();

    Expense updateExpense(Expense expense);

    void deleteExpenseById(Long id);

    long count();

    void deleteAllExpenses();
}
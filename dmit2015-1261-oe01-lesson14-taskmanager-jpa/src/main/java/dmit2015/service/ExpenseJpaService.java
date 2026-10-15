package dmit2015.service;

import dmit2015.model.Expense;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Named("jakartaPersistenceExpenseService")
@ApplicationScoped
public class ExpenseJpaService implements ExpenseService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public Expense createExpense(Expense expense) {
        entityManager.persist(expense);
        return expense;
    }

    @Override
    public Optional<Expense> getExpenseById(Long id) {
        try {
            Expense querySingleResult = entityManager.find(Expense.class, id);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // id value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Expense> getAllExpenses() {
        return entityManager.createQuery("SELECT o FROM Expense o ", Expense.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Expense updateExpense(Expense expense) {

        Optional<Expense> optionalExpense = getExpenseById(expense.getId());
        if (optionalExpense.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", expense.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingExpense = optionalExpense.orElseThrow();
            // Update only properties that is editable by the end user

            existingExpense.setDescription(expense.getDescription());
            existingExpense.setAmount(expense.getAmount());
            existingExpense.setDate(expense.getDate());

            expense = entityManager.merge(existingExpense);
        }
        return expense;
    }

    @Override
    @Transactional
    public void deleteExpenseById(Long id) {
        Optional<Expense> optionalExpense = getExpenseById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records
            entityManager.remove(expense);
        } else {
            throw new RuntimeException("Could not find Expense with id: " + id);
        }
    }

    @Override
    public long count() {
        return (long) entityManager.createQuery("SELECT count(e) from Expense e")
                .getSingleResult();
    }

    @Override
    public void deleteAllExpenses() {
        entityManager.createQuery("DELETE FROM Expense e").executeUpdate();
    }

}
package com.financemanager.service;
import com.financemanager.entity.SavingsGoal; import com.financemanager.repository.SavingsGoalRepository; import org.springframework.stereotype.Service; import java.math.BigDecimal; import java.time.YearMonth;
@Service public class FinancialHealthService {
 private final FinanceService finance; private final SavingsGoalRepository goals;
 public FinancialHealthService(FinanceService f,SavingsGoalRepository g){finance=f;goals=g;}
 public int score(Long userId){YearMonth m=YearMonth.now();BigDecimal income=finance.getMonthlyIncome(userId,m),expense=finance.getMonthlyExpense(userId,m);double savings=income.signum()==0?0:Math.max(0,Math.min(1,income.subtract(expense).divide(income,4,java.math.RoundingMode.HALF_UP).doubleValue()));double progress=goals.findByUserId(userId).stream().mapToDouble(g->g.getTargetAmount().signum()==0?0:Math.min(1,g.getCurrentAmount().divide(g.getTargetAmount(),4,java.math.RoundingMode.HALF_UP).doubleValue())).average().orElse(0);return (int)Math.round(50*savings+30*(expense.compareTo(income)<=0?1:0)+20*progress);}
}

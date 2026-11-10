package org.delcom.ifs25010pbo2026springbootcashflow;

import org.delcom.ifs25010pbo2026springbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.ifs25010pbo2026springbootcashflow.domain.repository.ITransactionRepository;
import org.delcom.ifs25010pbo2026springbootcashflow.usecase.FinanceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Smoke test: memastikan context Spring naik dan wiring Clean Architecture di AppConfig benar.
 */
@SpringBootTest
class AppTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertNotNull(context);
    }

    @Test
    void beansAreWired() {
        assertNotNull(context.getBean(ITransactionRepository.class));
        assertNotNull(context.getBean(FinanceUseCase.class));
        assertNotNull(context.getBean(FinancePresenter.class));
    }

    @Test
    void beansAreSingletons() {
        assertSame(context.getBean(FinanceUseCase.class), context.getBean(FinanceUseCase.class));
    }
}

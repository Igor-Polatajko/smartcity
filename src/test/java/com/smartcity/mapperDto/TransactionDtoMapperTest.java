package com.smartcity.mapperDto;

import com.smartcity.domain.Transaction;
import com.smartcity.dto.TransactionDto;
import name.falgout.jeffrey.testing.junit.mockito.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class TransactionDtoMapperTest {

    private Transaction transaction;
    private TransactionDto transactionDto;

    private TransactionDtoMapper mapper = new TransactionDtoMapper();

    @BeforeEach
    public void init() {
        transaction = new Transaction(1L, 1L, 30000L, 2000L,
                LocalDateTime.now(), LocalDateTime.now());
        transactionDto = new TransactionDto(transaction.getId(), transaction.getTaskId(),
                transaction.getCurrentBudget(), transaction.getTransactionBudget(),
                transaction.getCreatedDate(), transaction.getUpdatedDate());
    }

    @Test
    public void testConvertDaoToDto() {
        assertThat(transaction).isEqualToIgnoringGivenFields(mapper.transactionToTransactionDto(transaction));
    }

    @Test
    public void testConvertDtoToDao() {
        assertThat(mapper.transactionDtoToTransaction(transactionDto)).isEqualToIgnoringGivenFields(transaction);
    }

}
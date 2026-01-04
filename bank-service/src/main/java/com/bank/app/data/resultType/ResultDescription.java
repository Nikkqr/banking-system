package com.bank.app.data.resultType;

/**
 * энам содержащий описания результатов
 *
 */
public enum ResultDescription
{
    TheUserIsSuccessfullyFound,
    TheUserIsNotFound,
    TheUserIsSuccessfullyAdded,
    TheUserIsSuccessfullyDeleted,
    TheAccountIsSuccessfullyFound,
    TheUserIsSuccessfullyUpdated,
    TheAccountIsNotFound,
    TheAccountIsSuccessfullyUpdated,
    TheAccountIsSuccessfullyAdded,
    TheAccountIsSuccessfullyDeleted,
    TheAccountIsAbsentInTheRepository,
    TheUserHasBeenSuccessfullyCreated,
    InformationOfTheUseSuccessfullyPrinted,
    TheAccountHasBeenSuccessfullyCreated,
    TheMoneyIsPutToAccount,
    TheMoneyIsWithdrawToAccount,
    BalanceIsChanged,
    ThereIsNotEnoughMoneyInTheAccount,
    SuccessfullyPrinted,
    Success,
    Error,
}

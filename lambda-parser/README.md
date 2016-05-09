Jinq功能精简修改：

org.jinq.jooq.transform.MethodChecker.isMethodSafe(...)中metamodel为null直接return：

        //todo - direct return.
        if(metamodel==null){
            return OperationSideEffect.NONE;
        }


org.jinq.jooq.transform.SymbExToColumns.virtualMethodCallValueJ(...)的else if (metamodel.isFieldGetterMethod(sig))分支修改为：

        //todo 仅修改了这一分支: 原文已注释
        else if (sig.name.startsWith("get")) {
            ColumnExpressions<?> newColumns = new ColumnExpressions<>(null);
            newColumns.columns.add(DSL.field(sig.name.replace("get", "")));
            // newColumns.columns.add(new
            // TableFieldImpl<Record,Integer>(sig.name.replace("get",
            // ""),SQLDataType.INTEGER,DSL.table("tname"),null,SQLDataType.INTEGER.getBinding()));
            return newColumns;
        }
package com.jimm0063.magi.debt.management.debtmanagementltesystem.infrastructure.model;

import com.jimm0063.magi.debt.management.debtmanagementltesystem.domain.enums.BulkItemAction;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BulkStatementUploadForm {
    private BulkItemAction defaultAction = BulkItemAction.UPDATE;
    private List<BulkStatementFileForm> rows = new ArrayList<>();
}

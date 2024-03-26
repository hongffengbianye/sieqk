package com.hj.it.sieqk.dao;

import com.hj.it.sieqk.vo.*;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface KqDao {

    void insertApply(List<Apply> list);

    void insertVacationDetail(List<VacationDetail> list);

    void insertWorkOvertime(List<WorkOvertime> list);

    void insertWorkOvertimeDeTaile(List<WorkOvertimeDeTail> list);

    void insertMakeUpCard(List<MakeUpCard> list);

    void insertMakeUpCardDetail(List<MakeUpCardDetail> list);

    List<Apply> findApply();
}

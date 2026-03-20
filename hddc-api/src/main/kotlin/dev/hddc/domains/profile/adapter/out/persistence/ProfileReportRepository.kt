package dev.hddc.domains.profile.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ProfileReportRepository : JpaRepository<ProfileReportEntity, Long>

interface ProfileLinkReportRepository : JpaRepository<ProfileLinkReportEntity, Long>

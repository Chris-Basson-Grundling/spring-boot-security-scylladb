package com.cbg.sbss.entity;

import com.datastax.oss.driver.api.mapper.annotations.CqlName;
import com.datastax.oss.driver.api.mapper.annotations.Entity;
import com.datastax.oss.driver.api.mapper.annotations.PartitionKey;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@CqlName("roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

  @PartitionKey
  @CqlName("id")
  private UUID id;

  @CqlName("name")
  private String name;
}
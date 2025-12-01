/*
 *    Copyright 2010-2025 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.domain;

import java.io.Serializable;

public class ProductCharacteristics implements Serializable {
  private static final long serialVersionUID = 1L;

  private String productId;
  private String careLevel;
  private String spaceRequirement;
  private String monthlyCost;
  private String allergyRisk;
  private String noiseLevel;
  private String activityLevel;
  private String socialNeed;
  private String trainingDifficulty;
  private Integer lifespan;
  private Boolean suitableForStudio;
  private Boolean suitableForApartment;
  private Boolean suitableForHouse;
  private Boolean suitableForBusy;
  private Boolean childFriendly;
  private String temperament;
  private String description;

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public String getCareLevel() {
    return careLevel;
  }

  public void setCareLevel(String careLevel) {
    this.careLevel = careLevel;
  }

  public String getSpaceRequirement() {
    return spaceRequirement;
  }

  public void setSpaceRequirement(String spaceRequirement) {
    this.spaceRequirement = spaceRequirement;
  }

  public String getMonthlyCost() {
    return monthlyCost;
  }

  public void setMonthlyCost(String monthlyCost) {
    this.monthlyCost = monthlyCost;
  }

  public String getAllergyRisk() {
    return allergyRisk;
  }

  public void setAllergyRisk(String allergyRisk) {
    this.allergyRisk = allergyRisk;
  }

  public String getNoiseLevel() {
    return noiseLevel;
  }

  public void setNoiseLevel(String noiseLevel) {
    this.noiseLevel = noiseLevel;
  }

  public String getActivityLevel() {
    return activityLevel;
  }

  public void setActivityLevel(String activityLevel) {
    this.activityLevel = activityLevel;
  }

  public String getSocialNeed() {
    return socialNeed;
  }

  public void setSocialNeed(String socialNeed) {
    this.socialNeed = socialNeed;
  }

  public String getTrainingDifficulty() {
    return trainingDifficulty;
  }

  public void setTrainingDifficulty(String trainingDifficulty) {
    this.trainingDifficulty = trainingDifficulty;
  }

  public Integer getLifespan() {
    return lifespan;
  }

  public void setLifespan(Integer lifespan) {
    this.lifespan = lifespan;
  }

  public Boolean getSuitableForStudio() {
    return suitableForStudio;
  }

  public void setSuitableForStudio(Boolean suitableForStudio) {
    this.suitableForStudio = suitableForStudio;
  }

  public Boolean getSuitableForApartment() {
    return suitableForApartment;
  }

  public void setSuitableForApartment(Boolean suitableForApartment) {
    this.suitableForApartment = suitableForApartment;
  }

  public Boolean getSuitableForHouse() {
    return suitableForHouse;
  }

  public void setSuitableForHouse(Boolean suitableForHouse) {
    this.suitableForHouse = suitableForHouse;
  }

  public Boolean getSuitableForBusy() {
    return suitableForBusy;
  }

  public void setSuitableForBusy(Boolean suitableForBusy) {
    this.suitableForBusy = suitableForBusy;
  }

  public Boolean getChildFriendly() {
    return childFriendly;
  }

  public void setChildFriendly(Boolean childFriendly) {
    this.childFriendly = childFriendly;
  }

  public String getTemperament() {
    return temperament;
  }

  public void setTemperament(String temperament) {
    this.temperament = temperament;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "ProductCharacteristics{" + "productId='" + productId + '\'' + ", careLevel='" + careLevel + '\''
        + ", spaceRequirement='" + spaceRequirement + '\'' + ", monthlyCost='" + monthlyCost + '\'' + ", allergyRisk='"
        + allergyRisk + '\'' + ", noiseLevel='" + noiseLevel + '\'' + ", activityLevel='" + activityLevel + '\''
        + ", socialNeed='" + socialNeed + '\'' + ", trainingDifficulty='" + trainingDifficulty + '\'' + ", lifespan="
        + lifespan + ", suitableForStudio=" + suitableForStudio + ", suitableForApartment=" + suitableForApartment
        + ", suitableForHouse=" + suitableForHouse + ", suitableForBusy=" + suitableForBusy + ", childFriendly="
        + childFriendly + ", temperament='" + temperament + '\'' + ", description='" + description + '\'' + '}';
  }
}

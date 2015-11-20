chiselVersion	?= 2.3-SNAPSHOT
SBT		?= sbt
SBT_FLAGS	?= -Dsbt.log.noformat=true -DchiselVersion=$(chiselVersion)
TARGETDIR ?= ./target
OPENSOC_FLAGS	?= --harnessName OpenSoC_CMesh_NeighborTester_VarInjRate --moduleName OpenSoC_CMesh_Flit --C 1 --numVCs 2 --K 2,2 --Dim 2 --injRate 10 --targetDir $(TARGETDIR)

.PHONY:	smoke publish-local check clean jenkins-build

default:	publish-local

smoke:
	$(SBT) $(SBT_FLAGS) compile

publish-local:
	$(SBT) $(SBT_FLAGS) publish-local

check:
	$(SBT) $(SBT_FLAGS) "run --sw true $(OPENSOC_FLAGS)"
	$(SBT) $(SBT_FLAGS) "run --hw true $(OPENSOC_FLAGS)"

clean:
	$(SBT) $(SBT_FLAGS) clean
	for dir in $(CLEAN_DIRS); do $(MAKE) -C $$dir clean; done
	$(RM) -r $(RM_DIRS)

jenkins-build: clean check publish-local


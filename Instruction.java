/* File: Instruction.java
 * Authors: Marc Sun Bog & Simon Amtoft Pedersen
 *
 * The following file contains one RV32I instruction and all the possible fields
 */


package RISCVSimulator;

public class Instruction {
    int instruction, opcode, rd, rs1, rs2, funct3, funct7, immI, immS, immB, immU, immJ;
    String assemblyString;

    public Instruction(int instruction) {
        this.instruction = instruction;
        this.opcode = instruction & 0x7F;
        this.rd = (instruction >>7) & 0x1F;      // Returns bits 11 to 7
        this.funct3 = (instruction>>12) & 0x7;   // Returns bits 14 to 12
        this.funct7 = (instruction>>25) & 0x7F;  // Returns bits 31 to 25
        this.rs1 = (instruction >>15) & 0x1F;    // Returns bits 19 to 15
        this.rs2 = (instruction>>20) & 0x1F;     // Returns bits 24 to 20
        this.immI = (instruction>>20);           // Returns bits 31 to 20
        this.immS = rd | (funct7<<5); // Returns bits 31 to 25 and 11 to 7
        this.immB = getImmB(instruction);
        this.immU = instruction & 0xFFFFF000;
        this.immJ = getImmJ(instruction);
        this.assemblyString = toAssemblyString();
    }

    private String toAssemblyString(){
        String instr = "", arg1 = "", arg2 = "", arg3 = "";
        switch(opcode){
            // R-type instructions
            case 0b0110011: // ADD / SUB / SLL / SLT / SLTU / XOR / SRL / SRA / OR / AND
                arg1 = String.format("x%02d", rd);
                arg2 = String.format("x%02d", rs1);
                arg3 = String.format("x%02d", rs2);
                switch(funct3){
                    case 0b000: // ADD / SUB
                        switch(funct7){
                            case 0b0000000: // ADD
                                instr = "add";
                                break;
                            case 0b0100000: // SUB
                                instr = "sub";
                        }
                        break;
                    case 0b001: // SLL
                        instr = "sll";
                        break;
                    case 0b010: // SLT
                        instr = "slt";
                        break;
                    case 0b011: // SLTU
                        instr = "sltu";
                        break;
                    case 0b100: // XOR
                        instr = "xor";
                        break;
                    case 0b101: // SRL / SRA
                        switch(funct7){
                            case 0b0000000: // SRL
                                instr = "srl";
                                break;
                            case 0b0100000: // SRA
                                instr = "sra";
                                break;
                        }
                        break;
                    case 0b110: // OR
                        instr = "or";
                        break;
                    case 0b111: // AND
                        instr = "and";
                }
                break;
            case 0b1101111: //JAL
                arg1 = String.format("x%02d", rd);
                arg2 = String.format("x%x", immJ);
                instr = "jal";
                break;
            case 0b1100111: // JALR
                arg1 = String.format("x%02d", rd);
                arg2 = String.format("x%x", immI);
                instr = "jalr";
                break;
            case 0b0000011: // LB / LH / LW / LBU / LHU
                arg1 = String.format("x%d", rd);
                arg2 = String.format("%d(x%d)", immI, rs1);
                switch(funct3){
                    case 0b000: // LB
                        instr = "lb";
                        break;
                    case 0b001: // LH
                        instr = "lh";
                        break;
                    case 0b010: // LW
                        instr = "lw";
                        break;
                    case 0b100: // LBU
                        instr = "lbu";
                        break;
                    case 0b101: // LHU
                        instr = "lhu";
                        break;
                }
                break;
            case 0b0010011: // ADDI / SLTI / SLTIU / XORI / ORI / ANDI / SLLI / SRLI / SRAI
                arg1 = String.format("x%d", rd);
                arg2 = String.format("x%d", rs1);
                arg3 = String.format("%d", immI);
                switch(funct3){
                    case 0b000: // ADDI
                        instr = "addi";
                        break;
                    case 0b010: // SLTI
                        instr = "slti";
                        break;
                    case 0b011: // SLTIU
                        instr = "sltiu";
                        break;
                    case 0b100: // XORI
                        instr = "xori";
                        break;
                    case 0b110: // ORI
                        instr = "ori";
                        break;
                    case 0b111: // ANDI
                        instr = "andi";
                        break;
                    case 0b001: // SLLI
                        instr = "slli";
                        break;
                    case 0b101: // SRLI / SRAI
                        switch(funct7){
                            case 0b0000000: // SRLI
                                instr = "srli";
                                break;
                            case 0b0100000: // SRAI
                                instr = "srai";
                                break;
                        }
                        break;
                }
                break;
            case 0b0001111: // FENCE / FENCE.I
                switch(funct3){
                    case 0b000: // FENCE
                        instr = "fence";
                        break;
                    case 0b001: // FENCE.I
                        instr = "fence.i";
                        break;
                }
                break;
            case 0b1110011: // ECALL / EBREAK / CSRRW / CSRRS / CSRRC / CSRRWI / CSRRSI / CSRRCI
                switch(funct3){
                    case 0b000: // ECALL / EBREAK
                        switch(immI){
                            case 0b000000000000: // ECALL
                                instr = "ecall";
                                break;
                            case 0b000000000001: // EBREAK
                                instr = "ebreak";
                                break;
                        }
                        break;
                    case 0b001: // CSRRW
                        instr = "csrrw";
                        break;
                    case 0b010: // CSRRS
                        instr = "csrrs";
                        break;
                    case 0b011: // CSRRC
                        instr = "csrrc";
                        break;
                    case 0b101: // CSRRWI
                        instr = "csrrwi";
                        break;
                    case 0b110: // CSRRSI
                        instr = "csrrsi";
                        break;
                    case 0b111: // CSRRCI
                        instr = "csrrci";
                        break;
                }
                break;

            //S-type instructions
            case 0b0100011: //SB / SH / SW
                arg1 = String.format("x%d", rs2);
                arg2 = String.format("%d(x%d)", immS, rs1);
                switch(funct3){
                    case 0b000: // SB
                        instr = "sb";
                        break;
                    case 0b001: // SH
                        instr = "sh";
                        break;
                    case 0b010: // SW
                        instr = "sw";
                        break;
                }
                break;

            //B-type instructions
            case 0b1100011: // BEQ / BNE / BLT / BGE / BLTU / BGEU
                arg1 = String.format("x%d", rs1);
                arg2 = String.format("x%d", rs2);
                arg3 = String.format("%d", immB);
                switch(funct3){
                    case 0b000: // BEQ
                        instr = "beq";
                        break;
                    case 0b001: // BNE
                        instr = "bne";
                        break;
                    case 0b100: // BLT
                        instr = "blt";
                        break;
                    case 0b101: // BGE
                        instr = "bge";
                        break;
                    case 0b110: //BLTU
                        instr = "bltu";
                        break;
                    case 0b111: //BLGEU
                        instr = "blgeu";
                        break;
                }
                break;

            //U-type instructions
            case 0b0110111: //LUI
                arg1 = String.format("x%d", rd);
                arg2 = String.format("%d", immU>>>12);
                instr = "lui";
                break;
            case 0b0010111: //AUIPC
                arg1 = String.format("x%d", rd);
                arg2 = String.format("%d", immU >>> 12);
                instr = "auipc";
                break;
            default:
                return "Unimplemented opcode";
        }
        return String.format("%s %s %s %s", instr, arg1, arg2, arg3);
    }

    private int getImmB (int instruction) {
        int b11 = (instruction>>7) & 0x1;       // 11'th bit of immediate (7th bit of instruction)
        int b1to4 = (instruction>>8) & 0xF;     // Bits 1 to 4 of immediate (8 to 11 of instruction)
        int b5to10 = (instruction>>25) & 0x1F;  // Bits 5 to 10 of immediate (25 to 30 of instruction)
        int b12 = (instruction>>31) & 0x1;      // Bit 12 of immediate (MSB of instruction)

        // Returns bits in the order: imm[12|10:5|4:1|11]
        return b11 << 11 | b1to4 << 1 | b5to10 << 5 | b12 << 12;
    }

    private int getImmJ(int instruction) {
        int b12to19 = (instruction>>12) & 0xFF; // Bits 12 to 19 of immediate (12 to 19 of instruction)
        int b11 = (instruction>>20) & 0x1;      // Bit 11 of immediate (20th bit of instruction)
        int b1to10 = (instruction>>21) & 0x3FF; // Bit 1 to 10 of immediate (21 to 30 of instruction)
        int b20 = (instruction>>31);            // Bit 20 of immediate (MSB of instruction)

        // Returns bits in the order: imm[20|10:1|11|19:12]
        return (b20 << 20 | b12to19 << 12 | b11 << 11 | b1to10 << 1);
    }
}


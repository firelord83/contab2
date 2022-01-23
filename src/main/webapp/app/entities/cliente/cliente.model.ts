import { IFattureAttivo } from 'app/entities/fatture-attivo/fatture-attivo.model';

export interface ICliente {
  id?: number;
  nomeCliente?: string;
  partitaIVA?: string | null;
  indirizzo?: string | null;
  tipo?: string | null;
  fatturas?: IFattureAttivo[] | null;
}

export class Cliente implements ICliente {
  constructor(
    public id?: number,
    public nomeCliente?: string,
    public partitaIVA?: string | null,
    public indirizzo?: string | null,
    public tipo?: string | null,
    public fatturas?: IFattureAttivo[] | null
  ) {}
}

export function getClienteIdentifier(cliente: ICliente): number | undefined {
  return cliente.id;
}
